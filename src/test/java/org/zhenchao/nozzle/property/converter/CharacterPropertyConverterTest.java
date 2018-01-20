package org.zhenchao.nozzle.property.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.zhenchao.nozzle.exception.ConfigurationException;

public class CharacterPropertyConverterTest extends BaseConverterTestCase {

    private CharacterPropertyConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new CharacterPropertyConverter();
    }

    @Test
    public void testConvertPropertyValue() throws ConfigurationException {
        assertEquals(' ', converter.convert(" ", beanPropertyInfo).charValue());
        assertEquals('a', converter.convert("a", beanPropertyInfo).charValue());
        assertEquals('B', converter.convert("B", beanPropertyInfo).charValue());
        assertEquals('$', converter.convert("$", beanPropertyInfo).charValue());
    }

    @Test
    public void testConvertPropertyValueEmptyOrNull() throws ConfigurationException {
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testConvertPropertyValueFullString() throws ConfigurationException {
        // will still convert strings, but just uses the first character
        assertEquals('T', converter.convert("This is longer string", beanPropertyInfo).charValue());
        assertEquals(' ', converter.convert("  This is an untrimmed string  ", beanPropertyInfo).charValue());
    }

}
