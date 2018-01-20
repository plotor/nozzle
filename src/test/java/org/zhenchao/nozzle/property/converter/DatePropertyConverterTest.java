package org.zhenchao.nozzle.property.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.when;
import org.zhenchao.nozzle.Attribute;
import org.zhenchao.nozzle.Configurable;
import org.zhenchao.nozzle.exception.ConfigurationException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatePropertyConverterTest extends BaseConverterTestCase {

    private DatePropertyConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new DatePropertyConverter();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void setupBeanPropertyInfo() {
        super.setupBeanPropertyInfo();
        when(beanPropertyInfo.getPropertyName()).thenReturn("myDateProperty");
        when(beanPropertyInfo.getBeanType()).thenReturn((Class) Object.class);
    }

    @Test(expected = ConfigurationException.class)
    public void testNoDateFormat() throws ConfigurationException {
        converter.convert("2010", beanPropertyInfo);
    }

    @Test(expected = ConfigurationException.class)
    public void testNullDateFormat() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat(null));

        converter.convert("2010", beanPropertyInfo);
    }

    @Test(expected = ConfigurationException.class)
    public void testInvalidDateFormat() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("this isn't a real date format"));

        converter.convert("2010", beanPropertyInfo);
    }

    @Test(expected = ConfigurationException.class)
    public void testInvalidDate() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("yyyy"));

        converter.convert("This is not a parsable date", beanPropertyInfo);
    }

    @Test
    public void testConvertPropertyValue() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("yyyy"));

        assertEquals(createDate("2010", "yyyy"), converter.convert("2010", beanPropertyInfo));
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testConvertPropertyValueAnnotationAtField() throws ConfigurationException {
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockConfigurableDateFormat("yyyy"));

        assertEquals(createDate("2010", "yyyy"), converter.convert("2010", beanPropertyInfo));
    }

    @Test
    public void testConvertPropertyValueAnnotationAtFieldOverridesBeanAnnotation() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("yyyy"));
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockConfigurableDateFormat("yyyyMMdd"));

        assertEquals(createDate("20101223", "yyyyMMdd"), converter.convert("20101223", beanPropertyInfo));
    }

    private Date createDate(String date, String format) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (Exception exc) {
            return null;
        }
    }

}
