package org.zhenchao.nozzle.core;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link Properties} 对象构造器
 *
 * @author zhenchao.wang 2017-09-06 13:33:54
 * @version 1.0.0
 */
public class PropertiesBuilder {

    private static final Pattern PATTERN = Pattern.compile("\\$\\{[^\\s]+\\}");

    private final Map<String, String> props;
    private Environment environment;
    private boolean generated; // 标识是否已经完成 build

    public PropertiesBuilder() {
        this.environment = EnvironmentAccessor.getInstance().getEnvironment();
        this.props = new HashMap<String, String>();
        this.generated = false;
    }

    /**
     * 添加一个环境变量属性到当前构造器
     * 如果对应的值不存在，则会添加一个空字符串
     *
     * @param name
     * @return
     */
    public PropertiesBuilder addEnvironmentProperty(String name) {
        return this.addEnvironmentProperty(name, null);
    }

    /**
     * 添加一个环境变量属性到当前构造器，如果不存在则使用默认值代替
     *
     * @param name
     * @param defaultValue 默认值
     * @return
     */
    public PropertiesBuilder addEnvironmentProperty(String name, String defaultValue) {
        return this.addProperty(name, environment.getEnvVariable(name, defaultValue));
    }

    /**
     * 添加一个系统属性到当前构造器中
     * 如果对应的值不存在，则会添加一个空字符串
     *
     * @param propertyName
     * @return
     */
    public PropertiesBuilder addSystemProperty(String propertyName) {
        return this.addSystemProperty(propertyName, null);
    }

    /**
     * 添加一个系统属性到当前构造器中，如果不存在则使用默认值代替
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public PropertiesBuilder addSystemProperty(String name, String defaultValue) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("system property name is missing");
        }
        return this.addProperty(name, environment.getSystemProperty(name, defaultValue));
    }

    /**
     * 添加一个属性到当前构造器对象，如果输入的 value 是 null，则用空字符串替代
     *
     * @param name
     * @param value
     * @return
     */
    public PropertiesBuilder addProperty(String name, String value) {
        if (generated) {
            throw new IllegalStateException("The properties object for this builder has already been built.");
        }
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("property name is missing");
        }
        this.props.put(name, StringUtils.trimToEmpty(value));
        return this;
    }

    /**
     * 批量添加所有的环境变量到构造器
     *
     * @return
     */
    public PropertiesBuilder addAllEnvironmentProperties() {
        return this.addAll(environment.getAllEnvProperties());
    }

    /**
     * 批量添加所有的系统属性到当前构造器
     *
     * @return
     */
    public PropertiesBuilder addAllSystemProperties() {
        return this.addAll(environment.getAllSystemProperties());
    }

    /**
     * 批量添加属性到当前构造器
     *
     * @param props
     * @return
     */
    @SuppressWarnings("rawtypes")
    public PropertiesBuilder addAll(Map props) {
        for (Object key : props.keySet()) {
            Object val = props.get(key);
            this.addProperty(key.toString(), val != null ? String.valueOf(val) : null);
        }
        return this;
    }

    /**
     * 从 {@link InputStream} 对象中加载配置
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public PropertiesBuilder readAll(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("input stream is null");
        }
        Properties props = new Properties();
        props.load(inputStream);
        this.addAll(props);
        return this;
    }

    public Properties build() {
        if (generated) {
            throw new IllegalStateException("The build() method has already been called on this instance.");
        }
        this.generated = true; // 标记已 build
        Properties properties = new Properties();
        for (String key : this.props.keySet()) {
            properties.setProperty(key, getFilteredValue(key));
        }
        return properties;
    }

    int size() {
        if (generated) {
            throw new IllegalStateException("The properties object for this builder has already been built.");
        }
        return this.props.size();
    }

    String getProperty(String name) {
        if (generated) {
            throw new IllegalStateException("The properties object for this builder has already been built.");
        }
        return this.props.get(name);
    }

    String getFilteredValue(String key) {
        String value = this.props.get(key);
        if (StringUtils.isBlank(value)) {
            return "";
        }
        Matcher matcher = PATTERN.matcher(value);
        while (matcher.find()) {
            String grouping = matcher.group();
            String valKey = grouping.substring(2, grouping.length() - 1);

            String prop = getFilteredValue(valKey);
            if (!StringUtils.isEmpty(prop)) {
                value = matcher.replaceFirst(prop);
                matcher = PATTERN.matcher(value);
            }
        }
        return value;
    }

    Environment getEnvironment() {
        return environment;
    }

    void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
