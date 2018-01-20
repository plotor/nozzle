package org.zhenchao.nozzle.property.converter;

import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.property.BeanPropertyInfo;

/**
 * 类属性转换器
 *
 * @author zhenchao.wang 2016-09-07 09:19:22
 * @version 1.0.0
 */
public interface PropertyConverter<T> {

    /**
     * 当前转换器所支持的转换类型
     *
     * @return
     */
    Class<?> getSupportedClass();

    /**
     * 属性值转化，将 {@link String} 类型转换成目标类型
     *
     * @param value
     * @param propertyInfo
     * @return
     */
    T convert(String value, BeanPropertyInfo propertyInfo) throws ConfigurationException;
}
