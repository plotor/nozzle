package org.zhenchao.nozzle.property;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * bean 属性描述信息
 *
 * @author zhenchao.wang 2017-09-05 17:39:50
 * @version 1.0.0
 */
public interface BeanPropertyInfo {

    String getPropertyName();

    Class<?> getBeanType();

    Class<?> getPropertyType();

    boolean isPrimitive();

    boolean isArray();

    /**
     * 获取修饰属性的注解集合，包括属性的直接注解和修饰在 getter 和 setter 上的注解
     *
     * @return
     */
    Collection<Annotation> getPropertyAnnotations();

    /**
     * 获取修饰 bean 的注解集合（包括父类）
     *
     * @return
     */
    Collection<Annotation> getBeanAnnotations();
}
