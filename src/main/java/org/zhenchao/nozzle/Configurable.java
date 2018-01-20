package org.zhenchao.nozzle;

import static org.zhenchao.nozzle.constant.ConfigurationConstants.DEFAULT_RESOURCE_NAME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基本注入配置，
 * 用于指定配置文件名称和作用的目标类
 *
 * @author zhenchao.wang 2016-09-06 09:07:44
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Configurable {

    /** 资源文件名称 */
    String resource() default DEFAULT_RESOURCE_NAME;

    /** 资源文件注入的 bean，默认就是注解当前作用的类 */
    Class<?> injectClass() default Configurable.class;

}
