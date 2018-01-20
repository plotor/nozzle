package org.zhenchao.nozzle.source.provider;

import org.apache.commons.io.IOUtils;
import org.zhenchao.nozzle.core.PropertiesBuilder;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.source.Source;
import org.zhenchao.nozzle.source.SourceProvider;

import java.io.InputStream;
import java.util.Properties;

/**
 * 默认 {@link SourceProvider} 实现，
 * 加载当前工程 classpath 下的配置文件
 *
 * @author zhenchao.wang 2016-09-06 14:13:25
 * @version 1.0.0
 */
public class ClasspathSourceProvider extends AbstractSourceProvider implements SourceProvider {

    private static final String PROVIDER_IDENTIFIER = "com.xiaomi.passport.config.default";

    @Override
    public String getProviderIdentifier() {
        return PROVIDER_IDENTIFIER;
    }

    @Override
    public Priority getPriority() {
        // 赋予最低优先级，一般都不会使用本 Provider
        return Priority.BACKUP;
    }

    @Override
    public boolean support(Source source) {
        return true;
    }

    @Override
    protected String getResourceName(Source source) {
        String resourceName = super.getResourceName(source);
        if (!resourceName.endsWith(".properties") && !resourceName.endsWith(".xml")) {
            resourceName = resourceName + ".properties";
        }
        return resourceName;
    }

    @Override
    protected Properties createProperties(
            Class<?> injectClass, String resourceName, PropertiesBuilder builder) throws ConfigurationException {
        InputStream in = null;
        try {
            in = this.getResourceAsStream(injectClass, resourceName);
            return builder.readAll(in).build();
        } catch (Throwable t) {
            throw new ConfigurationException(
                    String.format("Could not read configuration for %s using the reference class %s", resourceName, injectClass), t);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private InputStream getResourceAsStream(Class<?> referenceClass, String resourceName) {
        InputStream in = referenceClass.getResourceAsStream(resourceName);
        if (in == null) {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        }
        return in;
    }
}
