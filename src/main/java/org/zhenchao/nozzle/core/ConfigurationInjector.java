package org.zhenchao.nozzle.core;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhenchao.nozzle.Configurable;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.listener.InjectEventListener;
import org.zhenchao.nozzle.listener.UpdateEventListener;
import org.zhenchao.nozzle.property.BeanPropertySetter;
import org.zhenchao.nozzle.property.BeanPropertySetterFactory;
import org.zhenchao.nozzle.source.Source;
import org.zhenchao.nozzle.source.SourceProvider;
import org.zhenchao.nozzle.source.SourceProviderFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 配置注入引擎
 *
 * @author zhenchao.wang 2016-12-01 14:22:31
 * @version 1.0.0
 */
public class ConfigurationInjector {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationInjector.class);

    private static volatile ConfigurationInjector INSTANCE = new ConfigurationInjector();

    private final MultiValueMap sourceMap;

    private final Set<InjectEventListener> injectListeners;
    private final Set<UpdateEventListener> updateListeners;

    private PropertiesBuilderFactory builderFactory = new PropertiesBuilderFactory();

    private boolean enableRefresh = false;

    protected ConfigurationInjector() {
        this.injectListeners = new HashSet<InjectEventListener>();
        this.updateListeners = new HashSet<UpdateEventListener>();
        this.sourceMap = new MultiValueMap();
    }

    public static ConfigurationInjector getInstance() {
        return INSTANCE;
    }

    static void reset() {
        INSTANCE = new ConfigurationInjector();
    }

    public static Properties loadProperties(String filePath) throws ConfigurationException {
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(filePath));
            PropertiesBuilderFactory factory = new PropertiesBuilderFactory(true, true);
            return factory.newPropertiesBuilder().readAll(inputStream).build();
        } catch (IOException exc) {
            throw new ConfigurationException("Could not load properties from file: " + filePath, exc);
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    /**
     * 将配置文件中的值赋到 bean 对应属性上
     *
     * @param bean
     * @param properties
     * @throws ConfigurationException
     */
    public void configureBean(Object bean, Properties properties) throws ConfigurationException {
        this.injectProperties(bean, properties);
    }

    /**
     * 将配置文件中的值赋到 bean 对应属性上，不调用 PostConstruct 方法
     *
     * @param bean
     * @throws ConfigurationException
     */
    public void configureBean(Object bean) throws ConfigurationException {
        this.configureBean(bean, false);
    }

    /**
     * 将配置文件中的值赋到 bean 对应属性上
     *
     * @param bean
     * @param invokePostConstruct 是否立即调用 PostConstruct 方法
     * @throws ConfigurationException
     */
    public void configureBean(Object bean, boolean invokePostConstruct) throws ConfigurationException {
        this.configureBean(bean, null, invokePostConstruct);
    }

    /**
     * 将配置文件中的值赋到 bean 对应属性上
     *
     * @param bean
     * @param source
     * @param invokePostConstruct
     * @throws ConfigurationException
     */
    public void configureBean(Object bean, Source source, boolean invokePostConstruct) throws ConfigurationException {
        if (null == bean) {
            throw new IllegalArgumentException("config bean error, inject bean obj is null");
        }

        // 模板方法，遍历调用 Configuration 前置处理器
        this.invokeBeforeConfiguration(bean);

        if (null != source) {
            this.configureBeanObject(bean, source);
        } else {
            this.configureBeanObject(bean);
        }

        // 如果允许，立即调用 @PostConstruct 方法，解析配置
        if (invokePostConstruct) {
            this.invokePostConstruct(bean);
        }

        // 模板方法，调用 Configuration 后置处理器
        this.invokeAfterConfiguration(bean);
    }

    /**
     * 更新配置，可以注册监听器以获取更新操作前后的消息
     *
     * @param source
     * @throws ConfigurationException
     */
    @SuppressWarnings("unchecked")
    public void reload(Source source) throws ConfigurationException {
        if (!this.isEnableRefresh()) {
            log.warn("The refresh configuration is not enable, skip update, source[{}]", source.getResourceName());
            return;
        }
        log.info("Start to reload configuration, source[{}]", source.getResourceName());
        synchronized (this.sourceMap) {
            Collection<Object> beans = (Collection<Object>) this.sourceMap.remove(source);
            for (Object bean : beans) {
                log.info("Refresh the bean[{}] that inherited their configuration[{}].", bean.getClass(), source.getResourceName());
                // 模板方法，更新前置操作
                this.invokeBeforeUpdate(bean);

                this.configureBeanObject(bean, source);
                this.invokePostConstruct(bean);

                // 模板方法，更新后置操作
                this.invokeAfterUpdate(bean);
            }
        }
    }

    public PropertiesBuilderFactory getBuilderFactory() {
        return this.builderFactory;
    }

    public void setBuilderFactory(PropertiesBuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
    }

    public Properties loadProperties(Class<?> referenceClass, String resourceLocation) throws ConfigurationException {
        try {
            Source source = new Source(referenceClass, resourceLocation);
            SourceProvider provider = SourceProviderFactory.getInstance().getSourceProvider(source);

            return provider.getProperties(source, this.getBuilderFactory());
        } catch (Throwable t) {
            throw new ConfigurationException("Could not load the properties", t);
        }
    }

    private void injectProperties(Object bean, Properties properties) throws ConfigurationException {
        // 获取属性 setter 集合
        BeanPropertySetterFactory bpsFactory = new BeanPropertySetterFactory();
        Collection<BeanPropertySetter> setters = bpsFactory.getPropertySetters(bean);

        // 执行属性值注入
        for (BeanPropertySetter setter : setters) {
            setter.setProperty(properties);
        }
    }

    @PreDestroy
    public void invokePreDestroyAll() {
        SourceProviderFactory.getInstance().clearAssociations();
    }

    /**
     * 遍历调用子类和父类的所有 PostConstruct 注解的无参方法
     *
     * @param bean
     * @throws ConfigurationException
     */
    private void invokePostConstruct(Object bean) throws ConfigurationException {
        Class<?> beanClass = bean.getClass();
        log.info("Call 'PostConstruct' method, class[{}]", bean.getClass());
        do {
            for (Method method : beanClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    Type[] types = method.getGenericParameterTypes();
                    if (types.length == 0) { // 仅调用无参数的方法
                        try {
                            method.invoke(bean, (Object[]) null);
                        } catch (Exception e) {
                            throw new ConfigurationException(
                                    String.format("Could not invoke method[%s] of class[%s]", method.getName(), bean.getClass().getName()), e);
                        }
                    }
                }
            }
        } while ((beanClass = beanClass.getSuperclass()) != null);
    }

    public void registerInjectListener(InjectEventListener listener) {
        synchronized (injectListeners) {
            injectListeners.add(listener);
        }
    }

    public void registerUpdateListener(UpdateEventListener listener) {
        synchronized (updateListeners) {
            updateListeners.add(listener);
        }
    }

    public boolean removeInjectListener(InjectEventListener listener) {
        synchronized (injectListeners) {
            return this.injectListeners.remove(listener);
        }
    }

    public boolean removeUpdateListener(UpdateEventListener listener) {
        synchronized (updateListeners) {
            return this.updateListeners.remove(listener);
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<Source> getConfiguredSource() {
        return this.sourceMap.keySet();
    }

    public boolean isEnableRefresh() {
        return this.enableRefresh;
    }

    public void setEnableRefresh(boolean enableRefresh) {
        this.enableRefresh = enableRefresh;
    }

    /**
     * 基于给定的数据源解析注入属性配置值
     *
     * @param bean
     * @param source
     * @throws ConfigurationException
     */
    private void configureBeanObject(Object bean, Source source) throws ConfigurationException {
        // 获取数据源对应的 provider
        SourceProvider provider = SourceProviderFactory.getInstance().getSourceProvider(source);

        // 拉取配置
        Properties props = provider.getProperties(source, this.getBuilderFactory());

        // 属性注入
        this.injectProperties(bean, props);
        sourceMap.put(source, bean);
    }

    /**
     * 没有指定数据源，解析 {@link Configurable} 配置
     *
     * @param bean
     * @throws ConfigurationException
     */
    private void configureBeanObject(Object bean) throws ConfigurationException {
        this.configureBeanObject(bean, new Source(bean));
    }

    /**
     * 遍历调用 Configuration 前置处理器
     *
     * @param bean
     */
    private void invokeBeforeConfiguration(Object bean) {
        log.debug("Invoke before configuration of bean[{}]", bean.getClass());
        synchronized (this.injectListeners) {
            for (InjectEventListener listener : this.injectListeners) {
                listener.before(bean);
            }
        }
    }

    /**
     * 遍历调用 Configuration 后置处理器
     *
     * @param bean
     */
    private void invokeAfterConfiguration(Object bean) {
        log.debug("Invoke after configuration of bean[{}]", bean.getClass());
        synchronized (this.injectListeners) {
            for (InjectEventListener listener : this.injectListeners) {
                listener.after(bean);
            }
        }
    }

    /**
     * 遍历调用 update 前置处理器
     *
     * @param bean
     */
    private void invokeBeforeUpdate(Object bean) {
        log.debug("Invoke before update configuration of bean[{}]", bean.getClass());
        synchronized (this.updateListeners) {
            for (UpdateEventListener listener : this.updateListeners) {
                listener.before(bean);
            }
        }
    }

    /**
     * 遍历调用 update 后置处理器
     *
     * @param bean
     */
    private void invokeAfterUpdate(Object bean) {
        log.debug("Invoke after update configuration of bean[{}]", bean.getClass());
        synchronized (this.updateListeners) {
            for (UpdateEventListener listener : this.updateListeners) {
                listener.after(bean);
            }
        }
    }

}
