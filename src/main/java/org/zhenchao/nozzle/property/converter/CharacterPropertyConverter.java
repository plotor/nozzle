package org.zhenchao.nozzle.property.converter;

import org.apache.commons.lang3.StringUtils;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.property.BeanPropertyInfo;

/**
 * {@link Character} 类型属性转换器
 * 简单返回字符串的第一个字符
 *
 * @author zhenchao.wang 2017-09-06 10:08:21
 * @version 1.0.0
 */
public class CharacterPropertyConverter implements PropertyConverter<Character> {

    @Override
    public Class<Character> getSupportedClass() {
        return Character.class;
    }

    @Override
    public Character convert(String value, BeanPropertyInfo propertyInfo) throws ConfigurationException {
        return StringUtils.isEmpty(value) ? null : value.charAt(0);
    }

}
