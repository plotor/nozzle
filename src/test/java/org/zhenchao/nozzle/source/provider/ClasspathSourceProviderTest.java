package org.zhenchao.nozzle.source.provider;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.zhenchao.nozzle.BaseTestCase;
import org.zhenchao.nozzle.core.PropertiesBuilderFactory;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.source.Source;
import org.zhenchao.nozzle.source.SourceProvider;

public class ClasspathSourceProviderTest extends BaseTestCase {

    private final PropertiesBuilderFactory factory = new PropertiesBuilderFactory();

    @Test
    public void testGetProperties() throws Exception {
        SourceProvider provider = new ClasspathSourceProvider();
        assertNotNull(provider.getProperties(new Source(getClass(), "/log4j.properties"), this.factory));
    }

    @Test
    public void testGetPropertiesNullFactory() throws Exception {
        SourceProvider provider = new ClasspathSourceProvider();
        assertNotNull(provider.getProperties(new Source(getClass(), "/log4j.properties"), null));
    }

    @Test(expected = ConfigurationException.class)
    public void testGetPropertiesInvalidIdentifier() throws Exception {
        SourceProvider provider = new ClasspathSourceProvider();
        provider.getProperties(new Source(this), this.factory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPropertiesNullID() throws Exception {
        SourceProvider provider = new ClasspathSourceProvider();
        assertNotNull(provider.getProperties(null, this.factory));
    }
}
