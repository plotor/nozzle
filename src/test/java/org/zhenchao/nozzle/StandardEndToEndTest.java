package org.zhenchao.nozzle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zhenchao.nozzle.core.AnotherConfigurableObject;
import org.zhenchao.nozzle.core.ConfigurableObject;
import org.zhenchao.nozzle.core.ConfigurationInjector;
import org.zhenchao.nozzle.core.EnvironmentAccessor;
import org.zhenchao.nozzle.core.MockConfigurableObject;
import org.zhenchao.nozzle.exception.ConfigurationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class StandardEndToEndTest extends BaseTestCase {

    private ConfigurableObject configurableObject;

    private MockConfigurableObject mockConfigurableObject;

    private AnotherConfigurableObject anotherConfigurableObject;

    @BeforeClass
    public static void setupFilesystem() {
        setupMockEnvironment();
        createConfDir();
        Properties props = new Properties();
        props.put("value.1", "This is the first value");
        props.put("value.2", "This is the second value");

        try {
            System.err.println(getConfDir());
            OutputStream out = new FileOutputStream(new File(getConfDir(), "test.properties"));
            props.store(out, "this is a comment");
        } catch (IOException exc) {
            // do nothing
        }
    }

    @Before
    public void setup() throws ConfigurationException {

        configurableObject = new ConfigurableObject();
        mockConfigurableObject = new MockConfigurableObject();
        anotherConfigurableObject = new AnotherConfigurableObject();

        ConfigurationInjector.getInstance().configureBean(configurableObject, true);
        ConfigurationInjector.getInstance().configureBean(mockConfigurableObject, true);
        ConfigurationInjector.getInstance().configureBean(anotherConfigurableObject, true);
        ConfigurationInjector.getInstance().setEnableRefresh(true);
    }

    @Test
    public void testConfigurationOfConfigurableObject() {
        /*
         * This data is stored in MockConfSourceProvider
         *
         * myFiles=/tmp/file.txt, /tmp/text.txt number=23
         * floatingPointNumber=123.56 trueFalse=false name=Z-Carioca
         * oneMoreFloat=1.34 aByte=120 fieldMessage=This is a simple message
         * aCharacter=s property.message=This is a simple property message
         * another.long.value=500
         */
        assertEquals(2, mockConfigurableObject.getFiles().length);
        assertEquals(23, mockConfigurableObject.getNumber());
        assertEquals(123.56, mockConfigurableObject.getFloatingPointNumber(), 0);
        assertFalse(mockConfigurableObject.getTrueFalse());
        assertEquals("Hello Z-Carioca!", mockConfigurableObject.getMessage());
        assertEquals((byte) 120, mockConfigurableObject.getMyByte());
        assertEquals('s', mockConfigurableObject.getaCharacter());
        assertEquals(146.56, mockConfigurableObject.getBigNum(), 0);
        assertEquals("This is a simple property message", mockConfigurableObject.getPropMessage());
    }

    @Test
    public void testMockConfigurationOfConfigurableObject() {
        /*
         * This data is stored in the classpath.
         *
         * myFiles=/tmp/file.txt,/tmp/text.txt number=22
         * floatingPointNumber=123.56 trueFalse=true name=Z Carioca
         * oneMoreFloat=0.34 aByte=120 fieldMessage=This is a simple message
         * aCharacter=S property.message=There is a field which states:
         * ${fieldMessage} - ${oneMoreFloat} ${along} another.long.value=500
         */

        assertEquals(2, configurableObject.getFiles().length);
        assertEquals(22, configurableObject.getNumber());
        assertEquals(123.56, configurableObject.getFloatingPointNumber(), 0);
        assertTrue(configurableObject.getTrueFalse());
        assertEquals("Hello Z Carioca!", configurableObject.getMessage());
        assertEquals((byte) 120, configurableObject.getMyByte());
        assertEquals('S', configurableObject.getaCharacter());
        assertEquals(145.56, configurableObject.getBigNum(), 0);
        assertEquals("There is a field which states: This is a simple message - 0.34 ${along}", configurableObject.getPropMessage());
        System.err.println(configurableObject.getProperties().size());
        assertEquals("There is a field which states: This is a simple message - 0.34 ${along}", configurableObject.getProperties().getProperty("property.message"));
    }

    @Test
    public void testAnotherConfigurationOfConfigurableObject() throws ConfigurationException {
        /*
         * There are two potential sources of this information.
         *
         * There is a file in the classpath test.properties:
         *
         * value.1=Value One value.2=Value Two
         *
         * There is also a file on the file system test.properties:
         *
         * value.1=This is the first value value.2=This is the second value
         *
         * The file system values should be used instead of the classpath values.
         */

        System.err.println(EnvironmentAccessor.getInstance().getEnvironment().getSystemProperty("value.1"));
        assertEquals("This is the first value", anotherConfigurableObject.getFirstValue());
        assertEquals("This is the second value", anotherConfigurableObject.getSecondValue());
    }

}
