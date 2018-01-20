package org.zhenchao.nozzle.property.converter;

import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.property.BeanPropertyInfo;
import org.zhenchao.nozzle.property.BeanPropertySetterFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数组类型属性转换器
 *
 * @author zhenchao.wang 2016-09-07 09:07:44
 * @version 1.0.0
 */
public class ArrayPropertyConverter<T> implements PropertyConverter<T> {

    private static final Pattern PATTERN = Pattern.compile("(?:^|,)(\\\"(?:[^\\\"]+|\\\"\\\")*\\\"|[^,]*)");

    private final Class<T> arrayType;

    private ArrayPropertyConverter(Class<T> arrayType) {
        this.arrayType = arrayType;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> ArrayPropertyConverter<T> createNewArrayPropertyConverter(Class<T> arrayType) {
        return new ArrayPropertyConverter(arrayType);
    }

    @Override
    public Class<?> getSupportedClass() {
        return Array.newInstance(arrayType.getComponentType(), 0).getClass();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public T convert(String value, BeanPropertyInfo propertyInfo) throws ConfigurationException {
        PropertyConverter converter =
                PropertyConverterRegistry.getRegistry().getPropertyConverter(arrayType.getComponentType());

        // 执行转换逻辑
        String[] splitValues = this.split(value);
        T data = (T) Array.newInstance(arrayType.getComponentType(), splitValues.length);
        for (int i = 0; i < splitValues.length; i++) {
            Object itemVal = converter.convert(splitValues[i], propertyInfo);
            if (itemVal == null && arrayType.getComponentType().isPrimitive()) {
                itemVal = BeanPropertySetterFactory.getDefaultValue(arrayType.getComponentType());
            }
            Array.set(data, i, itemVal);
        }
        return data;
    }

    protected String[] split(String originalValue) {
        List<String> list = new ArrayList<String>();
        Matcher matcher = PATTERN.matcher(originalValue);
        StringBuilder quotedGroup = new StringBuilder();
        boolean inQuotedLoop = false;
        while (matcher.find()) {
            String group = matcher.group();
            if (!inQuotedLoop) {
                if (group.startsWith(",")) {
                    group = group.substring(1);
                }
                group = group.trim();
            }

            if (group.startsWith("\"")) {
                group = group.substring(1);
                if (group.endsWith("\"")) {
                    group = group.substring(0, group.length() - 1);
                } else {
                    quotedGroup.append(group);
                    inQuotedLoop = true;
                    continue;
                }
            }

            if (inQuotedLoop) {
                if (group.endsWith("\"")) {
                    quotedGroup.append(group.substring(0, group.length() - 1));
                    inQuotedLoop = false;
                    group = quotedGroup.toString();
                } else {
                    quotedGroup.append(group);
                    continue;
                }
            }

            list.add(group);
        }

        return list.toArray(new String[list.size()]);
    }

}
