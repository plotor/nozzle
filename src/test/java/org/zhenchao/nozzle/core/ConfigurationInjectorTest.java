package org.zhenchao.nozzle.core;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhenchao.nozzle.BaseTestCase;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.listener.InjectEventListener;
import org.zhenchao.nozzle.listener.UpdateEventListener;
import org.zhenchao.nozzle.source.Source;

import java.util.Properties;

public class ConfigurationInjectorTest extends BaseTestCase {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationInjectorTest.class);

    private ConfigurationInjector injector;

    @Before
    public void setUp() throws Exception {
        PropertyConfigurator.configure(getClass().getResource("/log4j.properties"));
        injector = ConfigurationInjector.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        ConfigurationInjector.reset();
    }

    @Test
    public void testGetInstance() {
        assertSame(ConfigurationInjector.getInstance(), ConfigurationInjector.getInstance());
    }

    @Test
    public void testConfigureBeanObject() throws Exception {
        ConfigurableObject obj = new ConfigurableObject();
        this.injector.configureBean(obj);
    }

    /*@Test
    @Ignore
    public void testListAndSetPropertyInject() throws Exception {
        ConfigurableObject obj = new ConfigurableObject();
        this.injector.configureBean(obj);
        List<String> list = obj.getList();
        for (final String str : list) {
            System.out.println(str);
        }
        Assert.assertEquals(2, list.size());

        Set<String> set = obj.getSet();
        for (final String str : set) {
            System.out.println(str);
        }
        Assert.assertEquals(9, set.size());
    }*/

    @Test(expected = IllegalArgumentException.class)
    public void testConfigureBeanObjectNull() throws Exception {
        this.injector.configureBean(null);
    }

    @Test(expected = ConfigurationException.class)
    public void testConfigureBeanObjectInvalid() throws Exception {
        this.injector.configureBean(new UnconfigurableObject());
    }

    @Test
    public void testReconfigureConfigureBeanObject() throws Exception {
        MockUpdateListener updateListener = new MockUpdateListener();
        this.injector.registerUpdateListener(updateListener);

        ConfigurableObject obj = new ConfigurableObject();
        this.injector.configureBean(obj);
        this.injector.setEnableRefresh(true);

        for (Source source : this.injector.getConfiguredSource()) {
            this.injector.reload(source);
        }
        assertEquals(1, updateListener.count);
        assertEquals(obj, updateListener.lastCompleted);
        updateListener.lastCompleted = null;

        this.injector.setEnableRefresh(false);
        for (Source source : this.injector.getConfiguredSource()) {
            this.injector.reload(source);
        }
        assertEquals(1, updateListener.count);
        assertNull(updateListener.lastCompleted);
    }

    @Test
    public void testNotFullyConfiguredBeanObject() throws Exception {
        NotFullyConfiguredObject nfc = new NotFullyConfiguredObject();
        this.injector.configureBean(nfc);

        assertEquals(0.34, nfc.anotherFloat, 0.000001);
        assertEquals(500, nfc.anotherLongValue);
        assertEquals(1780000, nfc.longValue);
        assertEquals('S', nfc.getaCharacter());
        assertEquals("This is a simple message", nfc.getFieldMessage());
        assertEquals(new Double(123.56), nfc.getFloatingPointNumber());
        assertEquals("Hello Z Carioca!", nfc.getMessage());
        assertEquals((byte) 120, nfc.getMyByte());
        assertEquals(Boolean.TRUE, nfc.getTrueFalse());

    }

    @Test
    public void testConfigureBeanObjectBoolean() throws Exception {
        MockInjectListener listener = new MockInjectListener();
        MockUpdateListener updateListener = new MockUpdateListener();
        this.injector.registerInjectListener(listener);
        this.injector.registerUpdateListener(updateListener);
        ConfigurableObject obj = new ConfigurableObject();
        this.injector.configureBean(obj, false);

        assertFalse(obj.getBigNum() == (obj.getNumber() + obj.getFloatingPointNumber()));

        this.injector.configureBean(obj, true);

        assertEquals(0.34, obj.anotherFloat, 0.000001);
        assertEquals(500, obj.anotherLongValue);
        assertEquals(1780000, obj.longValue);
        assertEquals('S', obj.getaCharacter());
        assertEquals("This is a simple message", obj.getFieldMessage());
        assertEquals(new Double(123.56), obj.getFloatingPointNumber());
        assertEquals("Hello Z Carioca!", obj.getMessage());
        assertEquals((byte) 120, obj.getMyByte());
        assertEquals(22, obj.getNumber());
        assertEquals("There is a field which states: This is a simple message - 0.34 ${along}", obj.getPropMessage());
        assertEquals(Boolean.TRUE, obj.getTrueFalse());
        assertTrue(obj.getBigNum() == (obj.getNumber() + obj.getFloatingPointNumber()));

        this.injector.configureBean(new BadPostConstructObject(), false);

        assertTrue(this.injector.removeInjectListener(listener));
        assertTrue(this.injector.removeUpdateListener(updateListener));
    }

    @Test(expected = ConfigurationException.class)
    public void testConfigurationBeanBadBean() throws Exception {
        this.injector.configureBean(new BadPostConstructObject(), true);
    }

    @Test
    public void testLoadProperties() throws Exception {
        Properties props = injector.loadProperties(ConfigurableObject.class, "configurableobject.properties");
        assertNotNull(props);
        assertEquals("There is a field which states: This is a simple message - 0.34 ${along}", props.getProperty("property.message"));
    }

    @Test(expected = ConfigurationException.class)
    public void testLoadPropertiesBadProps() throws Exception {
        injector.loadProperties(ConfigurableObject.class, "unconfigurableobject.properties");
    }

    private static class MockInjectListener implements InjectEventListener {
        public void before(Object bean) {
            log.debug("Starting Configuration: " + bean);
        }

        public void after(Object bean) {
            log.debug("Completed Configuration: " + bean);
        }
    }

    private static class MockUpdateListener implements UpdateEventListener {
        private Object lastCompleted;
        private int count = 0;

        public void before(Object bean) {
            log.debug("Starting Bean Update: " + bean);
        }

        public void after(Object bean) {
            log.debug("Completed Bean Update: " + bean);
            this.lastCompleted = bean;
            this.count++;
        }
    }
}