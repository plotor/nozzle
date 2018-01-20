package org.zhenchao.nozzle.core;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author zhenchao.wang 2016-09-06 14:36:14
 * @version 1.0.0
 */
public class EnvironmentAccessor {

    private static final EnvironmentAccessor INSTANCE = new EnvironmentAccessor();

    private Environment environment;

    private EnvironmentAccessor() {
        this.environment = new DefaultEnvironment();
    }

    public static EnvironmentAccessor getInstance() {
        return INSTANCE;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public static class DefaultEnvironment implements Environment {

        @Override
        public Map<String, String> getAllEnvProperties() {
            return Collections.unmodifiableMap(System.getenv());
        }

        @Override
        public Map<String, String> getAllSystemProperties() {
            Map<String, String> props = new HashMap<String, String>();
            for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
                props.put(entry.getKey().toString(), entry.getValue().toString());
            }
            return Collections.unmodifiableMap(props);
        }

        @Override
        public String getEnvVariable(String name) {
            return this.getEnvVariable(name, null);
        }

        @Override
        public String getEnvVariable(String name, String defaultValue) {
            return StringUtils.defaultIfBlank(this.getAllEnvProperties().get(name), defaultValue);
        }

        @Override
        public String getSystemProperty(String name) {
            return System.getProperty(name);
        }

        @Override
        public String getSystemProperty(String name, String defaultValue) {
            return System.getProperty(name, defaultValue);
        }
    }
}
