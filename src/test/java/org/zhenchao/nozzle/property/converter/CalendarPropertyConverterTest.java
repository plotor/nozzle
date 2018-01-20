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
import java.util.Calendar;

public class CalendarPropertyConverterTest extends BaseConverterTestCase {

    private CalendarPropertyConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new CalendarPropertyConverter();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void setupBeanPropertyInfo() {
        super.setupBeanPropertyInfo();
        when(beanPropertyInfo.getPropertyName()).thenReturn("myCalendarProperty");
        when(beanPropertyInfo.getBeanType()).thenReturn((Class) Object.class);
    }

    @Test(expected = ConfigurationException.class)
    public void testNoCalendarFormat() throws ConfigurationException {
        converter.convert("2010", beanPropertyInfo);
    }

    @Test(expected = ConfigurationException.class)
    public void testNullCalendarFormat() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat(null));

        converter.convert("2010", beanPropertyInfo);
    }

    @Test(expected = ConfigurationException.class)
    public void testInvalidCalendarFormat() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("this isn't a real date format"));

        converter.convert("2010", beanPropertyInfo);
    }

    @Test(expected = ConfigurationException.class)
    public void testInvalidCalendar() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("yyyy"));

        converter.convert("This is not a parsable date", beanPropertyInfo);
    }

    @Test
    public void testConvertPropertyValue() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("yyyy"));

        assertEquals(createCalendar("2010", "yyyy"), converter.convert("2010", beanPropertyInfo));
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testConvertPropertyValueAnnotationAtField() throws ConfigurationException {
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockConfigurableDateFormat("yyyy"));

        assertEquals(createCalendar("2010", "yyyy"), converter.convert("2010", beanPropertyInfo));
    }

    @Test
    public void testConvertPropertyValueAnnotationAtFieldOverridesBeanAnnotation() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockConfigurableDateFormat("yyyy"));
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockConfigurableDateFormat("yyyyMMdd"));

        assertEquals(createCalendar("20101223", "yyyyMMdd"), converter.convert("20101223", beanPropertyInfo));
    }

    private Calendar createCalendar(String date, String format) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat(format).parse(date));
            return cal;
        } catch (Exception exc) {
            return null;
        }
    }

}
