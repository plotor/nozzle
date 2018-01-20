package org.zhenchao.nozzle.property.converter;

import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.property.BeanPropertyInfo;

import java.lang.reflect.Constructor;

/**
 * 一般类型属性转换器
 *
 * @author zhenchao.wang 2017-09-06 10:10:50
 * @version 1.0.0
 */
public class GenericPropertyConverter implements PropertyConverter<Object> {

    @Override
    public Class<?> getSupportedClass() {
        return Object.class;
    }

    @Override
    public Object convert(String value, BeanPropertyInfo propertyInfo) throws ConfigurationException {
        try {
            Constructor<?> constructor = propertyInfo.getPropertyType().getConstructor(String.class);
            return constructor.newInstance(value);
        } catch (Exception exc) {
            throw new ConfigurationException(
                    String.format("Could instantiate instance of class %s: %s", propertyInfo.getPropertyType(), exc.getMessage()), exc);
        }
    }

}
