package org.zhenchao.nozzle.property.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.property.BeanPropertyInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PropertyConverterRegistryTest {

    private PropertyConverterRegistry registry;

    @Before
    public void setup() {
        registry = new PropertyConverterRegistry();
    }

    @Test
    public void testGetRegistry() {
        assertNotNull(PropertyConverterRegistry.getRegistry());
        assertSame(PropertyConverterRegistry.getRegistry(), PropertyConverterRegistry.getRegistry());
    }

    @Test
    public void testRegister() throws ConfigurationException {
        registry.register(new PropertyConverter<SimpleDateFormat>() {
            @Override
            public Class<SimpleDateFormat> getSupportedClass() {
                return SimpleDateFormat.class;
            }

            @Override
            public SimpleDateFormat convert(String value, BeanPropertyInfo propertyInfo) throws ConfigurationException {
                return new SimpleDateFormat(value);
            }
        });

        assertNotNull(registry.getPropertyConverter(SimpleDateFormat.class));
        assertEquals(SimpleDateFormat.class, registry.getPropertyConverter(SimpleDateFormat.class).getSupportedClass());
        assertEquals(Object.class, registry.getPropertyConverter(File.class).getSupportedClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNull() throws ConfigurationException {
        registry.register(null);
    }

    @Test(expected = ConfigurationException.class)
    public void testRegisterBad() throws ConfigurationException {
        registry.register(new PropertyConverter<SimpleDateFormat>() {
            @Override
            public Class<SimpleDateFormat> getSupportedClass() {
                return null;
            }

            @Override
            public SimpleDateFormat convert(String value, BeanPropertyInfo propertyInfo) throws ConfigurationException {
                return null;
            }
        });
    }

    @Test
    public void testGetPropertyConverter() {
        assertEquals(Boolean.class, registry.getPropertyConverter(boolean.class).getSupportedClass());
        assertEquals(Boolean.class, registry.getPropertyConverter(Boolean.class).getSupportedClass());
        assertEquals(Character.class, registry.getPropertyConverter(char.class).getSupportedClass());
        assertEquals(Character.class, registry.getPropertyConverter(Character.class).getSupportedClass());
        assertEquals(Byte.class, registry.getPropertyConverter(byte.class).getSupportedClass());
        assertEquals(Byte.class, registry.getPropertyConverter(Byte.class).getSupportedClass());
        assertEquals(Short.class, registry.getPropertyConverter(short.class).getSupportedClass());
        assertEquals(Short.class, registry.getPropertyConverter(Short.class).getSupportedClass());
        assertEquals(Integer.class, registry.getPropertyConverter(int.class).getSupportedClass());
        assertEquals(Integer.class, registry.getPropertyConverter(Integer.class).getSupportedClass());
        assertEquals(Long.class, registry.getPropertyConverter(long.class).getSupportedClass());
        assertEquals(Long.class, registry.getPropertyConverter(Long.class).getSupportedClass());
        assertEquals(Float.class, registry.getPropertyConverter(float.class).getSupportedClass());
        assertEquals(Float.class, registry.getPropertyConverter(Float.class).getSupportedClass());
        assertEquals(Double.class, registry.getPropertyConverter(double.class).getSupportedClass());
        assertEquals(Double.class, registry.getPropertyConverter(Double.class).getSupportedClass());
        assertEquals(String.class, registry.getPropertyConverter(String.class).getSupportedClass());
        assertEquals(Object.class, registry.getPropertyConverter(File.class).getSupportedClass());
        assertEquals(Object.class, registry.getPropertyConverter(SimpleDateFormat.class).getSupportedClass());
        assertEquals(Object.class, registry.getPropertyConverter(Object.class).getSupportedClass());
        assertEquals(Date.class, registry.getPropertyConverter(Date.class).getSupportedClass());
        assertEquals(Calendar.class, registry.getPropertyConverter(Calendar.class).getSupportedClass());
    }

    @Test
    public void testGetPropertyConverterForArray() {
        assertEquals((new boolean[0]).getClass(), registry.getPropertyConverter((new boolean[0]).getClass()).getSupportedClass());
        assertEquals((new Boolean[0]).getClass(), registry.getPropertyConverter((new Boolean[0]).getClass()).getSupportedClass());
        assertEquals((new char[0]).getClass(), registry.getPropertyConverter((new char[0]).getClass()).getSupportedClass());
        assertEquals((new Character[0]).getClass(), registry.getPropertyConverter((new Character[0]).getClass()).getSupportedClass());
        assertEquals((new byte[0]).getClass(), registry.getPropertyConverter((new byte[0]).getClass()).getSupportedClass());
        assertEquals((new Byte[0]).getClass(), registry.getPropertyConverter((new Byte[0]).getClass()).getSupportedClass());
        assertEquals((new short[0]).getClass(), registry.getPropertyConverter((new short[0]).getClass()).getSupportedClass());
        assertEquals((new Short[0]).getClass(), registry.getPropertyConverter((new Short[0]).getClass()).getSupportedClass());
        assertEquals((new int[0]).getClass(), registry.getPropertyConverter((new int[0]).getClass()).getSupportedClass());
        assertEquals((new Integer[0]).getClass(), registry.getPropertyConverter((new Integer[0]).getClass()).getSupportedClass());
        assertEquals((new long[0]).getClass(), registry.getPropertyConverter((new long[0]).getClass()).getSupportedClass());
        assertEquals((new Long[0]).getClass(), registry.getPropertyConverter((new Long[0]).getClass()).getSupportedClass());
        assertEquals((new float[0]).getClass(), registry.getPropertyConverter((new float[0]).getClass()).getSupportedClass());
        assertEquals((new Float[0]).getClass(), registry.getPropertyConverter((new Float[0]).getClass()).getSupportedClass());
        assertEquals((new double[0]).getClass(), registry.getPropertyConverter((new double[0]).getClass()).getSupportedClass());
        assertEquals((new Double[0]).getClass(), registry.getPropertyConverter((new Double[0]).getClass()).getSupportedClass());
        assertEquals((new String[0]).getClass(), registry.getPropertyConverter((new String[0]).getClass()).getSupportedClass());
        assertEquals((new File[0]).getClass(), registry.getPropertyConverter((new File[0]).getClass()).getSupportedClass());
        assertEquals((new SimpleDateFormat[0]).getClass(), registry.getPropertyConverter((new SimpleDateFormat[0]).getClass()).getSupportedClass());
        assertEquals((new Object[0]).getClass(), registry.getPropertyConverter((new Object[0]).getClass()).getSupportedClass());
        assertEquals((new Date[0]).getClass(), registry.getPropertyConverter((new Date[0]).getClass()).getSupportedClass());
        assertEquals((new Calendar[0]).getClass(), registry.getPropertyConverter((new Calendar[0]).getClass()).getSupportedClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPropertyConverterNull() {
        registry.getPropertyConverter(null);
    }
}
