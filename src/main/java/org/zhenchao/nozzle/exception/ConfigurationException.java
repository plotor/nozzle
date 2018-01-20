package org.zhenchao.nozzle.exception;

/**
 * @author zhenchao.wang 2017-09-05 16:29:19
 * @version 1.0.0
 */
public class ConfigurationException extends Exception {

    private static final long serialVersionUID = 1507481093440850642L;

    public ConfigurationException() {
        super();
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
