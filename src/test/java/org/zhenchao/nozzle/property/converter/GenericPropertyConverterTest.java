package org.zhenchao.nozzle.property.converter;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.zhenchao.nozzle.exception.ConfigurationException;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GenericPropertyConverterTest extends BaseConverterTestCase {

    private GenericPropertyConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new GenericPropertyConverter();
    }

    @Test
    public void testConvertProperty1() throws ConfigurationException {
        setPropertyType(File.class);

        assertEquals(new File("/tmp/test.txt"), converter.convert("/tmp/test.txt", beanPropertyInfo));
    }

    @Test
    public void testConvertProperty2() throws ConfigurationException {
        setPropertyType(SimpleDateFormat.class);

        assertEquals(new SimpleDateFormat("yyyy"), converter.convert("yyyy", beanPropertyInfo));
    }

    @Test
    public void testConvertProperty3() throws ConfigurationException {
        setPropertyType(BigInteger.class);

        assertEquals(new BigInteger("1000"), converter.convert("1000", beanPropertyInfo));
    }

    @Test(expected = ConfigurationException.class)
    public void testConvertPropertyBadValue() throws ConfigurationException {
        setPropertyType(BigInteger.class);
        converter.convert("", beanPropertyInfo);
    }

    @Test(expected = ConfigurationException.class)
    public void testConvertPropertyInvalidType() throws ConfigurationException {
        setPropertyType(Calendar.class);
        converter.convert("2010", beanPropertyInfo);
    }

}
