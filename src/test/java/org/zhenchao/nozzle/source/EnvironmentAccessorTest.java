package org.zhenchao.nozzle.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.zhenchao.nozzle.core.Environment;
import org.zhenchao.nozzle.core.EnvironmentAccessor;

import java.util.Map;

public class EnvironmentAccessorTest {

    private Environment environment;

    @Before
    public void setupEnvironment() {
        environment = EnvironmentAccessor.getInstance().getEnvironment();
    }

    @Test
    public void testGetAllEnvProperties() {
        Map<String, String> env = environment.getAllEnvProperties();
        assertNotNull(env);
        assertFalse(env.isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetAllEnvPropertiesImmutable() {
        Map<String, String> env = environment.getAllEnvProperties();
        env.clear();
    }

    @Test
    public void testGetAllSystemProperties() {
        Map<String, String> sys = environment.getAllSystemProperties();
        assertNotNull(sys);
        assertFalse(sys.isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetAllSystemPropertiesImmutable() {
        Map<String, String> sys = environment.getAllSystemProperties();
        sys.clear();
    }

    @Test
    public void testGetEnvVariable() {
        assertNull(environment.getEnvVariable("fake-env-variable-123456"));
    }

    @Test
    public void testGetEnvVariableWithDefault() {
        assertEquals("fake value", environment.getEnvVariable("fake-env-variable-123456", "fake value"));
    }

    @Test
    public void testGetSystemProperty() {
        assertNull(environment.getSystemProperty("fake-system-property-123456"));
        assertNotNull(environment.getSystemProperty("java.io.tmpdir"));
    }

    public void testGetSystemPropertyWithDefault() {
        assertEquals("fake value", environment.getSystemProperty("fake-system-property-123456", "fake value"));

        System.setProperty("another-sys-property", "value123");
        assertEquals("value123", environment.getSystemProperty("another-sys-property", "fake value"));
        System.clearProperty("another-sys-property");
    }

}
