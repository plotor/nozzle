package org.zhenchao.nozzle.source;

import org.zhenchao.nozzle.core.PropertiesBuilderFactory;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.source.provider.ClasspathSourceProvider;

import java.util.Properties;

/**
 * 数据源 provider
 *
 * @author zhenchao.wang 2016-09-06 11:46:12
 * @version 1.0.0
 */
public interface SourceProvider {

    String SEP = ":";

    /**
     * 获取数据源 ID，需要保证唯一
     *
     * @return
     */
    String getProviderIdentifier();

    /**
     * 获取数据源优先级
     *
     * @return
     */
    Priority getPriority();

    /**
     * 解析 {@link Source} 所描述的数据源，转换成 {@link Properties} 对象
     *
     * @param source
     * @param builderFactory
     * @return
     * @throws ConfigurationException
     */
    Properties getProperties(Source source, PropertiesBuilderFactory builderFactory) throws ConfigurationException;

    /**
     * 当前 provider 是否支持指定的 {@link Source}
     *
     * @param source
     * @return
     */
    boolean support(Source source);

    void afterInit();

    void beforeDestroy();

    /**
     * 优先级定义，当存在多个数据源时，用于定义各个数据源的优先级
     */
    enum Priority {

        /** 最低优先级，应用于 {@link ClasspathSourceProvider} */
        BACKUP(-1),

        LOW(0),

        MEDIUM(1),

        HIGH(2),

        /** 最高优先级，可以覆盖掉低优先级的配置 */
        OVERRIDE(3);

        private int value;

        Priority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
