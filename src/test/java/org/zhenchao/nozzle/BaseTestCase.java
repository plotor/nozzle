package org.zhenchao.nozzle;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.zhenchao.nozzle.core.ConfigurationInjector;
import org.zhenchao.nozzle.core.EnvironmentAccessor;
import org.zhenchao.nozzle.core.MockEnvironment;

import java.io.File;
import java.io.IOException;

public class BaseTestCase {

    @BeforeClass
    public static void setupMockEnvironment() {
        EnvironmentAccessor.getInstance().setEnvironment(new MockEnvironment());
    }

    @AfterClass
    public static void resetNormalEnvironment() {
        ConfigurationInjector.getInstance().invokePreDestroyAll();
        // deleteConfDir();
        EnvironmentAccessor.getInstance().setEnvironment(new EnvironmentAccessor.DefaultEnvironment());
    }

    static File getConfDir() {
        String appRoot = EnvironmentAccessor.getInstance().getEnvironment().getEnvVariable("APP_ROOT");
        return new File(System.getProperty("java.io.tmpDir"), String.format("%s/conf", appRoot));
    }

    static void createConfDir() {
        getConfDir().mkdirs();
    }

    private static void deleteConfDir() {
        File confDir = getConfDir();
        try {
            if (confDir.exists()) {
                FileUtils.deleteDirectory(confDir);
            }
        } catch (IOException exc) {
            // do nothing
        }
    }
}
