package org.zhenchao.nozzle.property.converter;

import org.apache.commons.lang3.StringUtils;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.property.BeanPropertyInfo;

/**
 * {@link Boolean} 类型属性转换器
 *
 * @author zhenchao.wang 2017-09-06 10:06:15
 * @version 1.0.0
 */
public class BooleanPropertyConverter implements PropertyConverter<Boolean> {

    @Override
    public Class<Boolean> getSupportedClass() {
        return Boolean.class;
    }

    @Override
    public Boolean convert(String value, BeanPropertyInfo propertyInfo) throws ConfigurationException {
        return StringUtils.isBlank(value) ? null : this.parse(value.trim());
    }

    /**
     * 解析字符串，兼容主流 boolean 字面量
     *
     * @param value
     * @return
     * @throws ConfigurationException
     */
    private boolean parse(String value) throws ConfigurationException {
        boolean bool = Boolean.parseBoolean(value)
                || value.equalsIgnoreCase("yes")
                || value.equalsIgnoreCase("y")
                || value.equalsIgnoreCase("t")
                || value.equals("1");

        if (!bool) {
            if (this.isNotValid(value)) {
                throw new ConfigurationException(String.format("Could not parse the value %s as a boolean", value));
            }
        }
        return bool;
    }

    private boolean isNotValid(String value) {
        return !(value.equalsIgnoreCase("false")
                || value.equalsIgnoreCase("f")
                || value.equalsIgnoreCase("no")
                || value.equalsIgnoreCase("n")
                || value.equals("0"));
    }
}
