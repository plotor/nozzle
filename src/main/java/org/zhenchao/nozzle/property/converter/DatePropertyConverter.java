package org.zhenchao.nozzle.property.converter;

import org.apache.commons.lang3.StringUtils;
import org.zhenchao.nozzle.DatePattern;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.property.BeanPropertyInfo;

import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * {@link Date} 类型属性转换器
 *
 * @author zhenchao.wang 2017-09-06 10:09:05
 * @version 1.0.0
 */
public class DatePropertyConverter implements PropertyConverter<Date> {

    @Override
    public Class<?> getSupportedClass() {
        return Date.class;
    }

    @Override
    public Date convert(String value, BeanPropertyInfo propertyInfo) throws ConfigurationException {
        if (StringUtils.isBlank(value)) return null;

        // 获取日期格式化对象
        SimpleDateFormat sdf = this.getSimpleDateFormat(propertyInfo);
        if (sdf == null) {
            throw new ConfigurationException(
                    String.format("If convert a value to date, the field '%s' or class '%s' must be annotated with an @ConfigurableDateFormat",
                            propertyInfo.getPropertyName(), propertyInfo.getBeanType().getSimpleName()));
        }
        try {
            return sdf.parse(value);
        } catch (ParseException exc) {
            throw new ConfigurationException(
                    String.format("Could not format property %s of %s, value %s did not fit provided format %s",
                            propertyInfo.getPropertyName(), propertyInfo.getBeanType().getSimpleName(), value, sdf.toPattern()));
        }
    }

    /**
     * 基于注解配置的日期模板构造日期格式化对象
     *
     * @param beanPropertyInfo
     * @return
     * @throws ConfigurationException
     */
    protected SimpleDateFormat getSimpleDateFormat(BeanPropertyInfo beanPropertyInfo) throws ConfigurationException {
        DatePattern format = null;
        try {
            // 优先获取属性上的注解配置，获取不到在尝试获取属性隶属类的注解配置
            format = this.getConfigurableDateFormat(beanPropertyInfo.getPropertyAnnotations());
            if (format == null) {
                format = this.getConfigurableDateFormat(beanPropertyInfo.getBeanAnnotations());
            }

            // 构造日期格式话对象
            if (format != null) {
                return new SimpleDateFormat(format.value());
            }
        } catch (NullPointerException exc) {
            throw new ConfigurationException(
                    String.format("@ConfigurableDateFormat requires a format to be specified for property %s.%s",
                            beanPropertyInfo.getBeanType().getSimpleName(), beanPropertyInfo.getPropertyName()));
        } catch (IllegalArgumentException exc) {
            throw new ConfigurationException(
                    String.format("%s is not a valid format for the @ConfigurableDateFormat for property %s.%s", format == null ? null
                            : format.value(), beanPropertyInfo.getBeanType().getSimpleName(), beanPropertyInfo.getPropertyName()));
        }
        return null;
    }

    protected DatePattern getConfigurableDateFormat(Collection<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof DatePattern) {
                return (DatePattern) annotation;
            }
        }
        return null;
    }

}
