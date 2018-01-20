package org.zhenchao.nozzle.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.zhenchao.nozzle.BaseTestCase;

public class PropertiesBuilderFactoryTest extends BaseTestCase {

    @Test
    public void testPropertiesBuilderFactory() {
        PropertiesBuilderFactory factory = new PropertiesBuilderFactory();
        assertFalse(factory.isAddEnvironmentProperties());
        assertFalse(factory.isAddSystemProperties());
    }

    @Test
    public void testPropertiesBuilderFactoryFalseFalse() {
        PropertiesBuilderFactory factory = new PropertiesBuilderFactory(false, false);
        assertFalse(factory.isAddEnvironmentProperties());
        assertFalse(factory.isAddSystemProperties());
    }

    @Test
    public void testPropertiesBuilderFactoryFalseTrue() {
        PropertiesBuilderFactory factory = new PropertiesBuilderFactory(false, true);
        assertFalse(factory.isAddEnvironmentProperties());
        assertTrue(factory.isAddSystemProperties());
    }

    @Test
    public void testPropertiesBuilderFactoryTrueFalse() {
        PropertiesBuilderFactory factory = new PropertiesBuilderFactory(true, false);
        assertTrue(factory.isAddEnvironmentProperties());
        assertFalse(factory.isAddSystemProperties());
    }

    @Test
    public void testPropertiesBuilderFactoryTrueTrue() {
        PropertiesBuilderFactory factory = new PropertiesBuilderFactory(true, true);
        assertTrue(factory.isAddEnvironmentProperties());
        assertTrue(factory.isAddSystemProperties());
    }

    @Test
    public void testNewPropertiesBuilder() {
        PropertiesBuilder emptyBuilder = new PropertiesBuilderFactory().newPropertiesBuilder();
        assertEquals(0, emptyBuilder.size());

        PropertiesBuilder envBuilder = new PropertiesBuilderFactory(true, false).newPropertiesBuilder();
        assertEquals(2, envBuilder.size());

        PropertiesBuilder fullBuilder = new PropertiesBuilderFactory(true, true).newPropertiesBuilder();
        assertEquals(3, fullBuilder.size());
    }

}
