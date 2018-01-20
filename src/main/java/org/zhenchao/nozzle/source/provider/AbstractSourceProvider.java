package org.zhenchao.nozzle.source.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhenchao.nozzle.core.PropertiesBuilder;
import org.zhenchao.nozzle.core.PropertiesBuilderFactory;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.source.Source;
import org.zhenchao.nozzle.source.SourceProvider;

import java.util.Properties;

/**
 * @author zhenchao.wang 2016-09-06 13:25:20
 * @version 1.0.0
 */
public abstract class AbstractSourceProvider implements SourceProvider {

    protected static final Logger log = LoggerFactory.getLogger(AbstractSourceProvider.class);

    @Override
    public Properties getProperties(Source source, PropertiesBuilderFactory builderFactory) throws ConfigurationException {
        // 验证输入的数据源
        if (!this.validateSource(source)) {
            throw new IllegalArgumentException("illegal input source : " + source);
        }

        PropertiesBuilder builder = this.getPropertiesBuilder(builderFactory);
        Class<?> injectClass = source.getInjectClass();
        String resourceName = this.getResourceName(source);
        try {
            this.preHandle(source); // 模板方法
            return this.createProperties(injectClass, resourceName, builder);
        } finally {
            this.postHandle(source); // 模板方法
        }
    }

    public void afterInit() {
        // do nothing
    }

    public void beforeDestroy() {
        // do nothing
    }

    /**
     * 加载资源前给修改 {@link Source} 一次机会
     *
     * @param source
     */
    protected void preHandle(Source source) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("%s.beforeProcessAction(%s)", this.getClass(), source));
        }
    }

    /**
     * 加载资源后给修改 {@link Source} 一次机会
     *
     * @param source
     */
    protected void postHandle(Source source) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("%s.runPostProcessAction(%s)", getClass(), source));
        }
    }

    /**
     * 验证输入的 {@link Source}，子类可以覆盖增强
     *
     * @param source
     * @return
     */
    protected boolean validateSource(Source source) {
        return null != source;
    }

    /**
     * 基于给定的 {@link PropertiesBuilderFactory} 工厂，创建对应的 {@link PropertiesBuilder} 对象，
     * 如果输入为 null，则使用默认工厂
     *
     * @param builderFactory
     * @return
     */
    protected PropertiesBuilder getPropertiesBuilder(PropertiesBuilderFactory builderFactory) {
        if (builderFactory == null) {
            builderFactory = new PropertiesBuilderFactory();
        }
        return builderFactory.newPropertiesBuilder();
    }

    /**
     * 获取资源名称，子类可以覆盖本方法来实现一些定制化需求
     *
     * @param source
     * @return
     */
    protected String getResourceName(Source source) {
        return source.getResourceName();
    }

    /**
     * 基于配置值获取配置并封装到 {@link Properties} 对象中返回
     *
     * @param injectClass
     * @param resourceName
     * @param builder
     * @return
     * @throws ConfigurationException
     */
    protected abstract Properties createProperties(
            Class<?> injectClass, String resourceName, PropertiesBuilder builder) throws ConfigurationException;

}
