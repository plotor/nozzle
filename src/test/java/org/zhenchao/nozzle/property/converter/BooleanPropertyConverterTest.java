package org.zhenchao.nozzle.property.converter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.zhenchao.nozzle.exception.ConfigurationException;

public class BooleanPropertyConverterTest extends BaseConverterTestCase {

    private BooleanPropertyConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new BooleanPropertyConverter();
    }

    @Test
    public void testConvertPropertyValue() throws ConfigurationException {
        assertTrue(converter.convert("true", beanPropertyInfo));
        assertTrue(converter.convert("True", beanPropertyInfo));
        assertTrue(converter.convert("TRUE", beanPropertyInfo));
        assertTrue(converter.convert("T", beanPropertyInfo));
        assertTrue(converter.convert("t", beanPropertyInfo));
        assertTrue(converter.convert("yes", beanPropertyInfo));
        assertTrue(converter.convert("YES", beanPropertyInfo));
        assertTrue(converter.convert("Y", beanPropertyInfo));
        assertTrue(converter.convert("1", beanPropertyInfo));
        assertFalse(converter.convert("false", beanPropertyInfo));
        assertFalse(converter.convert("False", beanPropertyInfo));
        assertFalse(converter.convert("FALSE", beanPropertyInfo));
        assertFalse(converter.convert("F", beanPropertyInfo));
        assertFalse(converter.convert("f", beanPropertyInfo));
        assertFalse(converter.convert("no", beanPropertyInfo));
        assertFalse(converter.convert("NO", beanPropertyInfo));
        assertFalse(converter.convert("n", beanPropertyInfo));
        assertFalse(converter.convert("0", beanPropertyInfo));
    }

    @Test
    public void testConvertPropertyValueBlankOrNull() throws ConfigurationException {
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test(expected = ConfigurationException.class)
    public void testConvertPropertyValueBadValue() throws ConfigurationException {
        converter.convert("not a boolean", beanPropertyInfo);
    }

}
