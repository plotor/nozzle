package org.zhenchao.nozzle.property;

import org.zhenchao.nozzle.exception.ConfigurationException;

import java.util.Properties;

/**
 * bean 属性设置器
 *
 * @author zhenchao.wang 2017-09-05 17:38:43
 * @version 1.0.0
 */
public interface BeanPropertySetter {

    BeanPropertyInfo getBeanPropertyInfo();

    /**
     * 不同于 {@link BeanPropertyInfo#getPropertyName()}, 这里获取的是属性在 {@link Properties} 对象中的 key
     *
     * @return Returns the property key.
     */
    String getPropertyKey();

    /**
     * 基于 {@link Properties} 对象设置 bean 的属性
     *
     * @param properties
     * @throws ConfigurationException
     */
    void setProperty(Properties properties) throws ConfigurationException;
}
