package org.zhenchao.nozzle.property.converter;

import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.property.BeanPropertyInfo;

import java.util.Calendar;
import java.util.Date;

/**
 * {@link Calendar} 类型属性转换器, 基于 {@link DatePropertyConverter}
 *
 * @author zhenchao.wang 2017-09-07 10:06:15
 * @version 1.0.0
 */
public class CalendarPropertyConverter implements PropertyConverter<Calendar> {

    @Override
    public Class<Calendar> getSupportedClass() {
        return Calendar.class;
    }

    @Override
    public Calendar convert(String value, BeanPropertyInfo propertyInfo) throws ConfigurationException {
        Calendar calendar = null;
        DatePropertyConverter converter = new DatePropertyConverter();
        Date date = converter.convert(value, propertyInfo);
        if (date != null) {
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
        return calendar;
    }

}
