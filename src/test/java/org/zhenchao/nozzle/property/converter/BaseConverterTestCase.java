package org.zhenchao.nozzle.property.converter;

import org.junit.Before;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.zhenchao.nozzle.Attribute;
import org.zhenchao.nozzle.Configurable;
import org.zhenchao.nozzle.DatePattern;
import org.zhenchao.nozzle.NumberRadix;
import org.zhenchao.nozzle.property.BeanPropertyInfo;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;

/**
 * 基础测试类
 *
 * @author zhenchao.wang 2017-09-06 11:20:32
 * @version 1.0.0
 */
public abstract class BaseConverterTestCase {

    BeanPropertyInfo beanPropertyInfo;

    @Before
    public void init() throws Exception {
        beanPropertyInfo = mock(BeanPropertyInfo.class);
        setBeanAnnotations(mock(Configurable.class));
        setPropertyAnnotations(mock(Attribute.class));
        setupBeanPropertyInfo();
    }

    void setupBeanPropertyInfo() {
        // override
    }

    void setBeanAnnotations(Annotation... annotations) {
        when(beanPropertyInfo.getBeanAnnotations()).thenReturn(toCollection(annotations));
    }

    void setPropertyAnnotations(Annotation... annotations) {
        when(beanPropertyInfo.getPropertyAnnotations()).thenReturn(toCollection(annotations));
    }

    Collection<Annotation> toCollection(Annotation... annotations) {
        return Arrays.asList(annotations);
    }

    <T extends Annotation> T mockAnnotation(Class<T> annotationType) {
        return mock(annotationType);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void setPropertyType(Class type) {
        when(beanPropertyInfo.getPropertyType()).thenReturn(type);
    }

    NumberRadix mockNumberFormatAnnotation(NumberRadix.NumberFormat numberFormat) {
        NumberRadix configurableNumberEncoding = mockAnnotation(NumberRadix.class);

        when(configurableNumberEncoding.value()).thenReturn(numberFormat != null ? numberFormat : NumberRadix.NumberFormat.DECIMAL);

        return configurableNumberEncoding;
    }

    DatePattern mockConfigurableDateFormat(String format) {
        DatePattern configurableDateFormat = mockAnnotation(DatePattern.class);

        when(configurableDateFormat.value()).thenReturn(format);

        return configurableDateFormat;
    }

}
