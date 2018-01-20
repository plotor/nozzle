package org.zhenchao.nozzle.property.converter;

import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.property.BeanPropertyInfo;

/**
 * {@link String} 类型属性转换器
 *
 * @author zhenchao.wang 2017-09-06 10:10:40
 * @version 1.0.0
 */
public class StringPropertyConverter implements PropertyConverter<String> {

    @Override
    public Class<String> getSupportedClass() {
        return String.class;
    }

    @Override
    public String convert(String value, BeanPropertyInfo propertyInfo) throws ConfigurationException {
        return value;
    }

}
