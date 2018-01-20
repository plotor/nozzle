package org.zhenchao.nozzle.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.zhenchao.nozzle.BaseTestCase;
import org.zhenchao.nozzle.Configurable;
import org.zhenchao.nozzle.core.ConfigurationInjector;

public class SourceIdentifierTest extends BaseTestCase {

    @Test
    public void testHashCode() {
        Source id1 = new Source(getClass(), "/log4j.properties");
        Source id2 = new Source(getClass(), "/log4j.properties");
        Source id3 = new Source(getClass(), "log4j.properties");

        assertTrue(id1.hashCode() == id2.hashCode());
        assertFalse(id2.hashCode() == id3.hashCode());
    }

    @Test
    public void testEqualsObject() {
        Source id1 = new Source(getClass(), "/log4j.properties");
        Source id2 = new Source(getClass(), "/log4j.properties");
        Source id3 = new Source(getClass(), "log4j.properties");

        assertTrue(id1.equals(id1));
        assertTrue(id1.equals(id2));
        assertTrue(id2.equals(id1));
        assertFalse(id1 == id2);
        assertFalse(id1.equals(id3));
        assertFalse(id3.equals(id1));
        assertFalse(id3.equals(null));
        assertFalse(id3.equals(ConfigurationInjector.getInstance()));
    }

    @Test
    public void testConfigurationSourceIdentifierObject() {
        Source id = new Source(new NoAnnotationClass());

        assertSame(NoAnnotationClass.class, id.getInjectClass());
        assertEquals("noannotationclass", id.getResourceName());

        Source id2 = new Source(new SimpleAnnotationClass());

        assertSame(SimpleAnnotationClass.class, id2.getInjectClass());
        assertEquals("simpleannotationclass", id2.getResourceName());

        Source id3 = new Source(new RefClassAnnotationClass());

        assertSame(String.class, id3.getInjectClass());
        assertEquals("string", id3.getResourceName());

        Source id4 = new Source(new ResourceNameAnnotationClass());

        assertSame(ResourceNameAnnotationClass.class, id4.getInjectClass());
        assertEquals("/log4j", id4.getResourceName());

        Source id5 = new Source(new FullAnnotationClass());

        assertSame(String.class, id5.getInjectClass());
        assertEquals("/log4j", id5.getResourceName());
    }

    @Test
    public void testConfigurationSourceIdentifierClassOfQString() throws Exception {
        Source source = new Source(getClass(), "/log4j.properties");
        assertSame(getClass(), source.getInjectClass());
        assertEquals("/log4j.properties", source.getResourceName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigurationSourceIdentifierClassOfQStringNullNull() {
        new Source(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigurationSourceIdentifierClassOfQStringNull() {
        new Source(getClass(), null);
    }

    private static class NoAnnotationClass {
    }

    @Configurable
    private static class SimpleAnnotationClass {
    }

    @Configurable(injectClass = String.class)
    private static class RefClassAnnotationClass {
    }

    @Configurable(resource = "/log4j")
    private static class ResourceNameAnnotationClass {
    }

    @Configurable(injectClass = String.class, resource = "/log4j")
    private static class FullAnnotationClass {
    }

}
