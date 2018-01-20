package org.zhenchao.nozzle.source.provider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.zhenchao.nozzle.Attribute;
import org.zhenchao.nozzle.BaseTestCase;
import org.zhenchao.nozzle.Configurable;
import org.zhenchao.nozzle.core.ConfigurableObject;
import org.zhenchao.nozzle.core.Environment;
import org.zhenchao.nozzle.core.PropertiesBuilderFactory;
import org.zhenchao.nozzle.core.UnconfigurableObject;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.source.Source;
import static org.zhenchao.nozzle.source.provider.FilesystemSourceProvider.CONF_DIR_OVERRIDE;
import static org.zhenchao.nozzle.source.provider.FilesystemSourceProvider.DEFAULT_CONF_DIR;
import static org.zhenchao.nozzle.source.provider.FilesystemSourceProvider.DEFAULT_ROOT_DIR_ENV_VAR;
import static org.zhenchao.nozzle.source.provider.FilesystemSourceProvider.ROOT_DIR_ENV_OVERRIDE;
import static org.zhenchao.nozzle.source.provider.FilesystemSourceProvider.ROOT_DIR_OVERRIDE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public class FilesystemSourceProviderTest extends BaseTestCase {
    private static File appDir;
    private static File confDir;

    private Environment environment;
    private FilesystemSourceProvider fcsp;

    @BeforeClass
    public static void setupFiles() throws Exception {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        appDir = new File(tmpDir, "app_root");
        confDir = new File(appDir, "conf");

        confDir.mkdirs();

        File thisDir = new File(confDir, "net/zcarioca/zcommons/config/source/spi");
        thisDir.mkdirs();

        copyFile("org/zhenchao/nozzle/core/configurableobject.properties", new File(confDir, "configurableobject.properties"));
        copyFile("org/zhenchao/nozzle/core/test.properties", new File(thisDir, "test.properties"));
        copyFile("org/zhenchao/nozzle/source/baddata.properties", new File(confDir, "baddata.properties"));
    }

    private static void copyFile(String classpathResource, File file) throws Exception {
        InputStream in = ClassLoader.getSystemResourceAsStream(classpathResource);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

        IOUtils.copy(in, out);
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
    }

    @AfterClass
    public static void clearFiles() throws Exception {
        FileUtils.deleteDirectory(confDir);
    }

    @Before
    public void setupMock() throws Exception {
        environment = mock(Environment.class);
        when(environment.getEnvVariable("APP_ROOT")).thenReturn(appDir.getAbsolutePath());
        when(environment.getSystemProperty(ROOT_DIR_ENV_OVERRIDE, DEFAULT_ROOT_DIR_ENV_VAR)).thenReturn(DEFAULT_ROOT_DIR_ENV_VAR);
        when(environment.getSystemProperty(ROOT_DIR_OVERRIDE, null)).thenReturn(null);
        when(environment.getSystemProperty(CONF_DIR_OVERRIDE, DEFAULT_CONF_DIR)).thenReturn(DEFAULT_CONF_DIR);

        fcsp = new FilesystemSourceProvider(environment);
    }

    @After
    public void cleanup() throws Exception {
        fcsp.beforeDestroy();
    }

    @Test
    public void testGetResourceName() {
        assertEquals("filesystemsourceprovidertest", fcsp.getResourceName(new Source(this)));
        assertEquals("configuration.properties", fcsp.getResourceName(new Source(getClass(), "configuration.properties")));
        assertEquals("configuration.xml", fcsp.getResourceName(new Source(getClass(), "configuration.xml")));
    }

    @Test
    public void testGetProperties() throws ConfigurationException {
        assertNotNull(fcsp.getProperties(new Source(new ConfigurableObject()), new PropertiesBuilderFactory(false, false)));
    }

    @Test
    @Ignore
    public void testGetPropertiesNested() throws ConfigurationException {
        assertNotNull(fcsp.getProperties(new Source(new TestOne()), new PropertiesBuilderFactory(false, false)));
    }

    @Test
    public void testGetMonitoredConfigurationDirectory() {
        assertEquals(confDir.getAbsolutePath(), fcsp.getMonitoredConfigurationDirectory());
    }

    @Test
    public void testBadMonitoredConfigurationDirectory() {
        when(environment.getEnvVariable("APP_ROOT")).thenReturn(null);
        assertNull(fcsp.getMonitoredConfigurationDirectory());
    }

    @Test(expected = ConfigurationException.class)
    public void testBadGetProperties() throws ConfigurationException {
        when(environment.getEnvVariable("APP_ROOT")).thenReturn(null);
        fcsp.getProperties(new Source(new ConfigurableObject()), new PropertiesBuilderFactory());
    }

    @Test
    public void testGetMonitoredFiles() throws ConfigurationException {
        fcsp.getProperties(new Source(new ConfigurableObject()), new PropertiesBuilderFactory(false, false));
        Collection<File> monitoredFiles = fcsp.getMonitoredFiles();

        assertEquals(1, monitoredFiles.size());
        assertTrue(monitoredFiles.contains(new File(confDir, "configurableobject.properties")));
    }

    @Test
    public void testPostInit() {
        // should be able to call as many times as a want
        fcsp.afterInit();
        fcsp.afterInit();
        fcsp.afterInit();
    }

    @Test(expected = ConfigurationException.class)
    public void testNoConfigurationFile() throws ConfigurationException {
        fcsp.getProperties(new Source(new UnconfigurableObject()), new PropertiesBuilderFactory());
    }

    @Test(expected = ConfigurationException.class)
    public void testBadConfigurationFile() throws ConfigurationException {
        fcsp.getProperties(new Source(getClass(), "baddata.properties"), new PropertiesBuilderFactory());
    }

    @Test(expected = ConfigurationException.class)
    public void testMissingConfigurationFile() throws ConfigurationException {
        fcsp.getProperties(new Source(new TestTwo()), new PropertiesBuilderFactory());
    }

    @Configurable(resource = "test")
    public static class TestOne {
        @Attribute(name = "value.1")
        private String valueOne;

        @Attribute(name = "value.2")
        private String valueTwo;

        public String getValueOne() {
            return this.valueOne;
        }

        public void setValueOne(String valueOne) {
            this.valueOne = valueOne;
        }

        public String getValueTwo() {
            return this.valueTwo;
        }

        public void setValueTwo(String valueTwo) {
            this.valueTwo = valueTwo;
        }
    }

    @Configurable(resource = "test2.properties")
    private static class TestTwo extends TestOne {
        // no overrides
    }

}
