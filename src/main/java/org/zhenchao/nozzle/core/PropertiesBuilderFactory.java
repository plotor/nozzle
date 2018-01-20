package org.zhenchao.nozzle.core;

/**
 * {@link PropertiesBuilder} factory
 *
 * @author zhenchao.wang 2016-09-06 13:59:33
 * @version 1.0.0
 */
public final class PropertiesBuilderFactory {

    private boolean addEnvironmentProperties;
    private boolean addSystemProperties;

    public PropertiesBuilderFactory() {
        this(false, false);
    }

    public PropertiesBuilderFactory(boolean addEnvironmentProperties, boolean addSystemProperties) {
        this.addEnvironmentProperties = addEnvironmentProperties;
        this.addSystemProperties = addSystemProperties;
    }

    public PropertiesBuilder newPropertiesBuilder() {
        PropertiesBuilder builder = new PropertiesBuilder();
        if (this.isAddEnvironmentProperties()) {
            // 添加环境变量
            builder.addAllEnvironmentProperties();
        }
        if (this.isAddSystemProperties()) {
            // 添加系统变量
            builder.addAllSystemProperties();
        }
        return builder;
    }

    public boolean isAddEnvironmentProperties() {
        return this.addEnvironmentProperties;
    }

    public void setAddEnvironmentProperties(boolean addEnvironmentProperties) {
        this.addEnvironmentProperties = addEnvironmentProperties;
    }

    public boolean isAddSystemProperties() {
        return this.addSystemProperties;
    }

    public void setAddSystemProperties(boolean addSystemProperties) {
        this.addSystemProperties = addSystemProperties;
    }

}
