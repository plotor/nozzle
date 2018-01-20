package org.zhenchao.nozzle.property.converter;

import org.apache.commons.lang3.StringUtils;
import org.zhenchao.nozzle.NumberRadix;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.property.BeanPropertyInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 数值类型属性转换器
 *
 * @author zhenchao.wang 2017-09-06 10:10:45
 * @version 1.0.0
 */
public class NumberPropertyConverter<T extends Number> implements PropertyConverter<T> {

    private final Class<T> supportedClass;

    private NumberPropertyConverter(Class<T> type) {
        supportedClass = type;
    }

    public static <T extends Number> NumberPropertyConverter<T> createNewNumberPropertyConverter(Class<T> type) {
        return new NumberPropertyConverter<T>(type);
    }

    @Override
    public Class<T> getSupportedClass() {
        return supportedClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T convert(String value, BeanPropertyInfo propertyInfo) throws ConfigurationException {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            Method parse = this.getParseMethod();
            if (parse.getParameterTypes().length == 1) {
                return (T) parse.invoke(null, value.trim());
            }
            return (T) parse.invoke(null, value.trim(), this.getRadix(propertyInfo));
        } catch (Exception exc) {
            throw new ConfigurationException(String.format("Could not parse %s as a %s", value, getSupportedClass()), exc);
        }
    }

    /**
     * 获取对应的解析方法
     *
     * @return
     * @throws NoSuchMethodException
     */
    private Method getParseMethod() throws NoSuchMethodException {
        String methodName = "parse" + this.getSupportedClass().getSimpleName();
        if (this.getSupportedClass() == Integer.class) {
            methodName = "parseInt";
        }
        if (this.getSupportedClass() == Float.class || this.getSupportedClass() == Double.class) {
            return this.getSupportedClass().getMethod(methodName, String.class);
        }
        return this.getSupportedClass().getMethod(methodName, String.class, int.class);
    }

    private int getRadix(BeanPropertyInfo propertyInfo) {
        NumberRadix configurableNumberEncoding = this.getConfigurableNumberFormat(propertyInfo);
        return configurableNumberEncoding != null ? configurableNumberEncoding.value().radix() : 10;
    }

    /**
     * 获取当前属性的 ConfigurableNumberEncoding 注解配置，如果不存在则尝试从 bean 注解中
     *
     * @param propertyInfo
     * @return
     */
    private NumberRadix getConfigurableNumberFormat(BeanPropertyInfo propertyInfo) {
        NumberRadix configurableNumberEncoding = this.getConfigurableNumberFormat(propertyInfo.getPropertyAnnotations());
        return configurableNumberEncoding != null ? configurableNumberEncoding : this.getConfigurableNumberFormat(propertyInfo.getBeanAnnotations());
    }

    private NumberRadix getConfigurableNumberFormat(Collection<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof NumberRadix) {
                return (NumberRadix) annotation;
            }
        }
        return null;
    }

}
