package org.zhenchao.nozzle.property.converter;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import org.zhenchao.nozzle.exception.ConfigurationException;

public class StringPropertyConverterTest extends BaseConverterTestCase {

    private StringPropertyConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new StringPropertyConverter();
    }

    @Test
    public void testConvertPropertyValue() throws ConfigurationException {
        // the string property converter is just a pass through.
        assertSame("This is a string", converter.convert("This is a string", beanPropertyInfo));
        assertSame("", converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

}
