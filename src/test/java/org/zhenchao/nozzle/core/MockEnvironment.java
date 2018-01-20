package org.zhenchao.nozzle.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MockEnvironment implements Environment {
    private final Map<String, String> env = new HashMap<String, String>();
    private final Map<String, String> sys = new HashMap<String, String>();

    public MockEnvironment() {
        env.clear();
        env.put("fake-env-prop", "fake env value");
        env.put("APP_ROOT", new File(System.getProperty("java.io.tmpdir"), "app_root").getAbsolutePath());

        sys.clear();
        sys.put("fake.system.property", "fake value");
    }

    @Override
    public Map<String, String> getAllEnvProperties() {
        return env;
    }

    @Override
    public Map<String, String> getAllSystemProperties() {
        return sys;
    }

    @Override
    public String getEnvVariable(String name) {
        return getEnvVariable(name, null);
    }

    @Override
    public String getEnvVariable(String name, String defaultValue) {
        if (env.containsKey(name)) {
            return env.get(name);
        }
        return defaultValue;
    }

    @Override
    public String getSystemProperty(String name) {
        return getSystemProperty(name, null);
    }

    @Override
    public String getSystemProperty(String name, String defaultValue) {
        if (sys.containsKey(name)) {
            return sys.get(name);
        }
        return defaultValue;
    }

}
