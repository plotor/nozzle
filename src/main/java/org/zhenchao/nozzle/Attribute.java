package org.zhenchao.nozzle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性注入配置，
 * 指定属性键名、默认值，以及是否以原生 Properties 对象注入
 *
 * @author zhenchao.wang 2016-09-06 09:10:54
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Attribute {

    /** 配置键名 */
    String name() default "";

    /** 默认值 */
    String defaultValue() default "";

    /** 是否直接以 Properties 对象注入 */
    boolean raw() default false;

}
