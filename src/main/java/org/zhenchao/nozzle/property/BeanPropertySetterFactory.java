package org.zhenchao.nozzle.property;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhenchao.nozzle.Attribute;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.property.converter.PropertyConverter;
import org.zhenchao.nozzle.property.converter.PropertyConverterRegistry;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * {@link BeanPropertySetter} 工厂
 *
 * @author zhenchao.wang 2017-09-05 18:12:37
 * @version 1.0.0
 */
public class BeanPropertySetterFactory {

    private static final Logger log = LoggerFactory.getLogger(BeanPropertySetterFactory.class);

    public static Object getDefaultValue(Class<?> primitiveType) {
        if (boolean.class == primitiveType) {
            return Boolean.FALSE;
        }
        if (char.class == primitiveType) {
            return '\u0000';
        }
        if (float.class == primitiveType) {
            return 0f;
        }
        if (double.class == primitiveType) {
            return 0d;
        }
        if (byte.class == primitiveType) {
            return (byte) 0;
        }
        if (short.class == primitiveType) {
            return (short) 0;
        }
        if (int.class == primitiveType) {
            return 0;
        }
        return 0L;
    }

    private static Collection<Annotation> getPropertyAnnotations(Field field, PropertyDescriptor descriptor) {
        Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<Class<? extends Annotation>, Annotation>();

        if (descriptor != null) {
            if (descriptor.getWriteMethod() != null) {
                addAnnotationsToMap(annotations, descriptor.getWriteMethod().getAnnotations());
            }
            if (descriptor.getReadMethod() != null) {
                addAnnotationsToMap(annotations, descriptor.getReadMethod().getAnnotations());
            }
        }
        if (field != null) {
            addAnnotationsToMap(annotations, field.getAnnotations());
        }
        return annotations.values();
    }

    private static Collection<Annotation> getBeanAnnotations(Class<?> beanClass) {
        return Arrays.asList(beanClass.getAnnotations());
    }

    private static void addAnnotationsToMap(Map<Class<? extends Annotation>, Annotation> annotationMap, Annotation[] annotations) {
        if (ArrayUtils.isNotEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                if (!annotationMap.containsKey(annotation.annotationType())) {
                    annotationMap.put(annotation.annotationType(), annotation);
                }
            }
        }
    }

    /**
     * 构造指定 bean 的 {@link BeanPropertySetter} 集合
     * 如果存在 setter 方法，则执行 setter 注入，否则直接对属性进行赋值
     *
     * @param bean
     * @return {@link BeanPropertySetter} 集合
     * @throws ConfigurationException
     */
    public Collection<BeanPropertySetter> getPropertySetters(Object bean) throws ConfigurationException {
        List<BeanPropertySetter> setters = new ArrayList<BeanPropertySetter>();
        Map<String, PropertyDescriptor> descriptors = new HashMap<String, PropertyDescriptor>();
        Class<?> beanClass = bean.getClass();

        // 遍历属性被注解的 getter 和 setter 方法，构造对应的属性值注入器
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            // 遍历处理 bean 的属性
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                Method getter = descriptor.getReadMethod(); // getter
                Method setter = descriptor.getWriteMethod(); // setter

                if (getter != null) {
                    descriptors.put(descriptor.getDisplayName(), descriptor);
                }

                // 存在 setter 方法
                if (setter != null) {
                    Field field = this.getField(beanClass, descriptor);
                    if (setter.isAnnotationPresent(Attribute.class)) { // 注解在 setter 上
                        setters.add(new WriterBeanPropertySetter(bean, descriptor, field, setter.getAnnotation(Attribute.class)));
                        descriptors.remove(descriptor.getDisplayName());
                    }
                    if (getter != null && getter.isAnnotationPresent(Attribute.class)) { // 注解在 getter 上
                        setters.add(new WriterBeanPropertySetter(bean, descriptor, field, getter.getAnnotation(Attribute.class)));
                        descriptors.remove(descriptor.getDisplayName());
                    }
                }
            }
        } catch (Throwable t) {
            throw new ConfigurationException("could not introspect bean class : " + beanClass, t);
        }

        // 遍历被注解的属性，构造对应的属性值注入器
        do {
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Attribute.class)) {
                    if (descriptors.containsKey(field.getName())
                            && descriptors.get(field.getName()).getWriteMethod() != null) {
                        PropertyDescriptor desc = descriptors.get(field.getName());
                        setters.add(new WriterBeanPropertySetter(bean, desc, field, field.getAnnotation(Attribute.class)));
                    } else {
                        setters.add(new FieldBeanPropertySetter(bean, null, field, field.getAnnotation(Attribute.class)));
                    }
                } else if (descriptors.containsKey(field.getName())) {
                    // 未注解在属性上，而是注解在 getter 上
                    PropertyDescriptor desc = descriptors.get(field.getName());
                    if (desc.getReadMethod().isAnnotationPresent(Attribute.class)) {
                        setters.add(new FieldBeanPropertySetter(bean, desc, field, desc.getReadMethod().getAnnotation(Attribute.class)));
                    }
                }
            }
        } while ((beanClass = beanClass.getSuperclass()) != null);

        return setters;
    }

    private Field getField(Class<?> beanClass, PropertyDescriptor descriptor) {
        do {
            try {
                return beanClass.getDeclaredField(descriptor.getName());
            } catch (NoSuchFieldException exc) {
                // ignore, move on
            }
        } while ((beanClass = beanClass.getSuperclass()) != null);
        return null;
    }

    private static abstract class AbstractBeanPropertySetter implements BeanPropertySetter {

        final Object bean;
        final PropertyDescriptor descriptor;
        final Field field;
        final BeanPropertyInfo beanPropertyInfo;
        final Attribute attr;

        public AbstractBeanPropertySetter(Object bean, PropertyDescriptor descriptor, Field field, Attribute attr) {
            this.bean = bean;
            this.descriptor = descriptor;
            this.field = field;
            this.attr = attr;

            Class<?> propertyType = field != null ? field.getType() : descriptor.getPropertyType();
            String propertyName = field != null ? field.getName() : descriptor.getName();

            this.beanPropertyInfo = new BeanPropertyInfoImpl(bean.getClass(), propertyType, propertyName,
                    getBeanAnnotations(bean.getClass()), getPropertyAnnotations(field, descriptor));
        }

        @Override
        public BeanPropertyInfo getBeanPropertyInfo() {
            return this.beanPropertyInfo;
        }

        @Override
        public String getPropertyKey() {
            String propName = attr.name();
            if (StringUtils.isEmpty(propName)) {
                // 如果没有指定 key，就用属性名称代替
                propName = beanPropertyInfo.getPropertyName();
            }
            return propName;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public void setProperty(Properties properties) throws ConfigurationException {
            String propName = this.getPropertyKey();
            String defaultVal = attr.defaultValue(); // 默认值

            if (log.isTraceEnabled()) {
                log.trace("Setting property [{}] with value [{}] for bean [{}]", propName, defaultVal, bean.getClass());
            }

            try {
                Object beanVal;
                if (attr.raw()) { // 采用原生 properties 对象
                    beanVal = properties;
                } else {
                    Class<?> rawType = this.getRawType();
                    PropertyConverter converter = PropertyConverterRegistry.getRegistry().getPropertyConverter(rawType);
                    beanVal = converter.convert(properties.getProperty(propName, defaultVal), beanPropertyInfo);

                    // support Set and List type
                    /*if (null != beanVal && beanVal.getClass().isArray()) {
                        if (rawType.isAssignableFrom(Set.class)) {
                            String[] strs = (String[]) beanVal;
                            Set<String> set = new HashSet<String>();
                            if (ArrayUtils.isNotEmpty(strs)) {
                                for (final String str : strs) {
                                    if (StringUtils.isBlank(str)) continue;
                                    set.add(str.trim());
                                }
                            }
                            beanVal = Collections.unmodifiableSet(set);
                        } else if (rawType.isAssignableFrom(List.class)) {
                            String[] strs = (String[]) beanVal;
                            List<String> list = new ArrayList<String>();
                            if (ArrayUtils.isNotEmpty(strs)) {
                                for (final String str : strs) {
                                    if (StringUtils.isBlank(str)) continue;
                                    list.add(str.trim());
                                }
                            }
                            beanVal = Collections.unmodifiableList(list);
                        }
                    }*/

                    // 如果没有对应的值，则采用类型默认值注入
                    if (beanVal == null) {
                        if (!beanPropertyInfo.isArray() && beanPropertyInfo.isPrimitive()) {
                            beanVal = getDefaultValue(beanPropertyInfo.getPropertyType());
                        }
                    }
                }

                // 注入值
                this.injectValue(beanVal);
            } catch (Exception exc) {
                throw new ConfigurationException("Could not write property to bean : "
                        + beanPropertyInfo.getBeanType() + "#" + beanPropertyInfo.getPropertyName() + "(" + beanPropertyInfo.getPropertyType() + ")", exc);
            }
        }

        public abstract Class<?> getRawType();

        public abstract void injectValue(Object beanVal) throws IllegalAccessException, InvocationTargetException;
    }

    /**
     * 属性值注入
     *
     * @author zhenchao.wang 2016-09-06 11:12:29
     * @version 1.0.0
     */
    private static final class FieldBeanPropertySetter extends AbstractBeanPropertySetter implements BeanPropertySetter {

        public FieldBeanPropertySetter(Object bean, PropertyDescriptor descriptor, Field field, Attribute attr) {
            super(bean, descriptor, field, attr);
        }

        @Override
        public Class<?> getRawType() {
            return field.getType();
        }        @Override
        public void injectValue(Object beanVal) throws IllegalAccessException {
            field.setAccessible(true);
            field.set(bean, beanVal);
        }


    }

    /**
     * 属性 setter 方法注入
     *
     * @author zhenchao.wang 2016-09-06 11:12:51
     * @version 1.0.0
     */
    private static final class WriterBeanPropertySetter extends AbstractBeanPropertySetter implements BeanPropertySetter {

        public WriterBeanPropertySetter(Object bean, PropertyDescriptor descriptor, Field field, Attribute attr) {
            super(bean, descriptor, field, attr);
        }

        @Override
        public void injectValue(Object beanVal) throws IllegalAccessException, InvocationTargetException {
            descriptor.getWriteMethod().setAccessible(true);
            descriptor.getWriteMethod().invoke(bean, beanVal);
        }

        @Override
        public Class<?> getRawType() {
            return descriptor.getPropertyType();
        }
    }

    /**
     * @author zhenchao.wang 2016-09-06 11:00:13
     * @version 1.0.0
     */
    private static final class BeanPropertyInfoImpl implements BeanPropertyInfo {

        /** 属性隶属的 bean */
        private final Class<?> beanType;

        /** 属性类型 */
        private final Class<?> propertyType;

        /** 属性名称 */
        private final String propertyName;

        /** 属性隶属的 bean 所附加的注解集合 */
        private final Collection<Annotation> beanAnnotations;

        /** 属性附加的注解集合 */
        private final Collection<Annotation> propertyAnnotations;

        public BeanPropertyInfoImpl(Class<?> beanType, Class<?> propertyType, String propertyName,
                                    Collection<Annotation> beanAnnotations, Collection<Annotation> propertyAnnotations) {
            this.beanType = beanType;
            this.propertyType = propertyType;
            this.propertyName = propertyName;
            this.propertyAnnotations = Collections.unmodifiableCollection(propertyAnnotations);
            this.beanAnnotations = Collections.unmodifiableCollection(beanAnnotations);
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public Class<?> getBeanType() {
            return beanType;
        }

        @Override
        public Class<?> getPropertyType() {
            return isArray() ? propertyType.getComponentType() : propertyType;
        }

        @Override
        public boolean isPrimitive() {
            return getPropertyType().isPrimitive();
        }

        @Override
        public boolean isArray() {
            return propertyType.isArray();
        }

        @Override
        public Collection<Annotation> getPropertyAnnotations() {
            return propertyAnnotations;
        }

        @Override
        public Collection<Annotation> getBeanAnnotations() {
            return beanAnnotations;
        }
    }
}
