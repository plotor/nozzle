package org.zhenchao.nozzle.core;

import java.util.Map;

/**
 * 系统环境变量抽象
 *
 * @author zhenchao.wang 2017-09-06 13:35:35
 * @version 1.0.0
 */
public interface Environment {

    Map<String, String> getAllEnvProperties();

    Map<String, String> getAllSystemProperties();

    String getEnvVariable(String name);

    String getEnvVariable(String name, String defaultValue);

    String getSystemProperty(String name);

    String getSystemProperty(String name, String defaultValue);
}
