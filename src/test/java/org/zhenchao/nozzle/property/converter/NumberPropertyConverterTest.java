package org.zhenchao.nozzle.property.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.zhenchao.nozzle.Attribute;
import org.zhenchao.nozzle.Configurable;
import org.zhenchao.nozzle.NumberRadix;
import org.zhenchao.nozzle.exception.ConfigurationException;

public class NumberPropertyConverterTest extends BaseConverterTestCase {

    @Test
    public void testByte() throws ConfigurationException {
        NumberPropertyConverter<Byte> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Byte.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).byteValue());
        assertEquals(-128, converter.convert("-128", beanPropertyInfo).byteValue());
        assertEquals(127, converter.convert("127", beanPropertyInfo).byteValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test(expected = ConfigurationException.class)
    public void testByteOutOfRange() throws ConfigurationException {
        NumberPropertyConverter<Byte> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Byte.class);
        converter.convert("255", beanPropertyInfo);
    }

    @Test
    public void testShort() throws ConfigurationException {
        NumberPropertyConverter<Short> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Short.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).shortValue());
        assertEquals(567, converter.convert("567", beanPropertyInfo).shortValue());
        assertEquals(-2567, converter.convert("-2567", beanPropertyInfo).shortValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test(expected = ConfigurationException.class)
    public void testShortOutOfRange() throws ConfigurationException {
        NumberPropertyConverter<Short> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Short.class);
        converter.convert("99999999999", beanPropertyInfo);
    }

    @Test
    public void testInteger() throws ConfigurationException {
        NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).intValue());
        assertEquals(567000, converter.convert("567000", beanPropertyInfo).intValue());
        assertEquals(-2003567, converter.convert("-2003567", beanPropertyInfo).intValue());
        assertEquals(2000000000, converter.convert("2000000000", beanPropertyInfo).intValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testIntegerWithNoNumberFormat() throws ConfigurationException {
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockNumberFormatAnnotation(null));

        NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).intValue());
        assertEquals(567000, converter.convert("567000", beanPropertyInfo).intValue());
        assertEquals(-2003567, converter.convert("-2003567", beanPropertyInfo).intValue());
        assertEquals(2000000000, converter.convert("2000000000", beanPropertyInfo).intValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testIntegerWithDecimal() throws ConfigurationException {
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockNumberFormatAnnotation(NumberRadix.NumberFormat.DECIMAL));

        NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).intValue());
        assertEquals(567000, converter.convert("567000", beanPropertyInfo).intValue());
        assertEquals(-2003567, converter.convert("-2003567", beanPropertyInfo).intValue());
        assertEquals(2000000000, converter.convert("2000000000", beanPropertyInfo).intValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testIntegerWithBinary() throws ConfigurationException {
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockNumberFormatAnnotation(NumberRadix.NumberFormat.BINARY));

        NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).intValue());
        assertEquals(1, converter.convert("1", beanPropertyInfo).intValue());
        assertEquals(2, converter.convert("10", beanPropertyInfo).intValue());
        assertEquals(3, converter.convert("11", beanPropertyInfo).intValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testIntegerWithOctal() throws ConfigurationException {
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockNumberFormatAnnotation(NumberRadix.NumberFormat.OCTAL));

        NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).intValue());
        assertEquals(1, converter.convert("1", beanPropertyInfo).intValue());
        assertEquals(7, converter.convert("7", beanPropertyInfo).intValue());
        assertEquals(8, converter.convert("10", beanPropertyInfo).intValue());
        assertEquals(9, converter.convert("11", beanPropertyInfo).intValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testIntegerWithHex() throws ConfigurationException {
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockNumberFormatAnnotation(NumberRadix.NumberFormat.HEX));

        NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).intValue());
        assertEquals(10, converter.convert("a", beanPropertyInfo).intValue());
        assertEquals(15, converter.convert("f", beanPropertyInfo).intValue());
        assertEquals(16, converter.convert("10", beanPropertyInfo).intValue());
        assertEquals(255, converter.convert("FF", beanPropertyInfo).intValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testIntegerWithHexOnClass() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockNumberFormatAnnotation(NumberRadix.NumberFormat.HEX));

        NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).intValue());
        assertEquals(10, converter.convert("a", beanPropertyInfo).intValue());
        assertEquals(15, converter.convert("f", beanPropertyInfo).intValue());
        assertEquals(16, converter.convert("10", beanPropertyInfo).intValue());
        assertEquals(255, converter.convert("FF", beanPropertyInfo).intValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testIntegerWithHexOverridingClassAnnotation() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockNumberFormatAnnotation(NumberRadix.NumberFormat.BINARY));
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockNumberFormatAnnotation(NumberRadix.NumberFormat.HEX));

        NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).intValue());
        assertEquals(10, converter.convert("a", beanPropertyInfo).intValue());
        assertEquals(15, converter.convert("f", beanPropertyInfo).intValue());
        assertEquals(16, converter.convert("10", beanPropertyInfo).intValue());
        assertEquals(255, converter.convert("FF", beanPropertyInfo).intValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testIntegerWithNoFormatOverridingClassAnnotation() throws ConfigurationException {
        setBeanAnnotations(mockAnnotation(Configurable.class), mockNumberFormatAnnotation(NumberRadix.NumberFormat.BINARY));
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockNumberFormatAnnotation(null));

        NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).intValue());
        assertEquals(10, converter.convert("10", beanPropertyInfo).intValue());
        assertEquals(15, converter.convert("15", beanPropertyInfo).intValue());
        assertEquals(16, converter.convert("16", beanPropertyInfo).intValue());
        assertEquals(255, converter.convert("255", beanPropertyInfo).intValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test(expected = ConfigurationException.class)
    public void testIntegerInvalid() throws ConfigurationException {
        NumberPropertyConverter<Integer> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class);
        converter.convert("123.123412", beanPropertyInfo);
    }

    @Test
    public void testLong() throws ConfigurationException {
        NumberPropertyConverter<Long> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Long.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo).longValue());
        assertEquals(567000, converter.convert("567000", beanPropertyInfo).longValue());
        assertEquals(-2003567, converter.convert("-2003567", beanPropertyInfo).longValue());
        assertEquals(2000000000000000000l, converter.convert("2000000000000000000", beanPropertyInfo).longValue());
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test(expected = ConfigurationException.class)
    public void testLongInvalid() throws ConfigurationException {
        NumberPropertyConverter<Long> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Long.class);
        converter.convert("FFFF", beanPropertyInfo);
    }

    @Test
    public void testFloat() throws ConfigurationException {
        NumberPropertyConverter<Float> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Float.class);
        assertEquals(0f, converter.convert("0", beanPropertyInfo), 0);
        assertEquals(123.456f, converter.convert("123.456", beanPropertyInfo), 0);
        assertEquals(-256.99f, converter.convert("-256.99", beanPropertyInfo), 0);
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test(expected = ConfigurationException.class)
    public void testFloatInvalid() throws ConfigurationException {
        NumberPropertyConverter<Float> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Float.class);
        converter.convert("123.456.789", beanPropertyInfo);
    }

    @Test
    public void testDouble() throws ConfigurationException {
        NumberPropertyConverter<Double> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Double.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo), 0);
        assertEquals(55, converter.convert("55", beanPropertyInfo), 0);
        assertEquals(123.456, converter.convert("123.456", beanPropertyInfo), 0);
        assertEquals(-256.99, converter.convert("-256.99", beanPropertyInfo), 0);
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test
    public void testDoubleNumberFormatHasNoAffect() throws ConfigurationException {
        setPropertyAnnotations(mockAnnotation(Attribute.class), mockNumberFormatAnnotation(NumberRadix.NumberFormat.BINARY));

        NumberPropertyConverter<Double> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Double.class);
        assertEquals(0, converter.convert("0", beanPropertyInfo), 0);
        assertEquals(55, converter.convert("55", beanPropertyInfo), 0);
        assertEquals(123.456, converter.convert("123.456", beanPropertyInfo), 0);
        assertEquals(-256.99, converter.convert("-256.99", beanPropertyInfo), 0);
        assertNull(converter.convert(" ", beanPropertyInfo));
        assertNull(converter.convert("", beanPropertyInfo));
        assertNull(converter.convert(null, beanPropertyInfo));
    }

    @Test(expected = ConfigurationException.class)
    public void testDoubleInvalid() throws ConfigurationException {
        NumberPropertyConverter<Double> converter = NumberPropertyConverter.createNewNumberPropertyConverter(Double.class);
        converter.convert("123.456.789", beanPropertyInfo);
    }

}
