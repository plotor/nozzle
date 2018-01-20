package org.zhenchao.nozzle.property.converter;

import org.zhenchao.nozzle.exception.ConfigurationException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 属性转换器注册类
 *
 * @author zhenchao.wang 2016-09-06 09:44:26
 * @version 1.0.0
 */
public class PropertyConverterRegistry {

    private static final PropertyConverterRegistry INSTANCE = new PropertyConverterRegistry();

    private final Map<Class<?>, PropertyConverter<?>> registry;

    protected PropertyConverterRegistry() {
        registry = new HashMap<Class<?>, PropertyConverter<?>>();
        registry.put(String.class, new StringPropertyConverter());
        registry.put(Boolean.class, new BooleanPropertyConverter());
        registry.put(Character.class, new CharacterPropertyConverter());
        registry.put(Byte.class, NumberPropertyConverter.createNewNumberPropertyConverter(Byte.class));
        registry.put(Short.class, NumberPropertyConverter.createNewNumberPropertyConverter(Short.class));
        registry.put(Integer.class, NumberPropertyConverter.createNewNumberPropertyConverter(Integer.class));
        registry.put(Long.class, NumberPropertyConverter.createNewNumberPropertyConverter(Long.class));
        registry.put(Float.class, NumberPropertyConverter.createNewNumberPropertyConverter(Float.class));
        registry.put(Double.class, NumberPropertyConverter.createNewNumberPropertyConverter(Double.class));
        registry.put(Date.class, new DatePropertyConverter());
        registry.put(Calendar.class, new CalendarPropertyConverter());
    }

    public static PropertyConverterRegistry getRegistry() {
        return INSTANCE;
    }

    public void register(PropertyConverter<?> converter) throws ConfigurationException {
        if (null == converter) {
            throw new IllegalArgumentException("register converter cannot be null");
        }
        Class<?> supportedClass = converter.getSupportedClass();
        if (supportedClass != null) {
            this.registry.put(supportedClass, converter);
        } else {
            throw new ConfigurationException("converter " + converter.getClass().getName() + " has no supported class");
        }
    }

    public PropertyConverter<?> getPropertyConverter(Class<?> type) {
        if (null == type) {
            throw new IllegalArgumentException("input type cannot be null");
        }

        if (type.isArray()) {
            return ArrayPropertyConverter.createNewArrayPropertyConverter(type);
        }

        /*if (List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type)) {
            return ArrayPropertyConverter.createNewArrayPropertyConverter(String[].class);
        }*/

        Class<?> normalizedType = this.normalizedType(type);
        if (this.registry.containsKey(normalizedType)) {
            return this.registry.get(normalizedType);
        }

        return new GenericPropertyConverter();
    }

    private Class<?> normalizedType(Class<?> type) {
        if (type.isPrimitive()) {
            return this.convertFromPrimitiveType(type);
        }
        return type;
    }

    private Class<?> convertFromPrimitiveType(Class<?> type) {
        if (boolean.class == type) {
            return Boolean.class;
        }
        if (char.class == type) {
            return Character.class;
        }
        if (byte.class == type) {
            return Byte.class;
        }
        if (short.class == type) {
            return Short.class;
        }
        if (int.class == type) {
            return Integer.class;
        }
        if (long.class == type) {
            return Long.class;
        }
        if (float.class == type) {
            return Float.class;
        }
        return Double.class;
    }

}
