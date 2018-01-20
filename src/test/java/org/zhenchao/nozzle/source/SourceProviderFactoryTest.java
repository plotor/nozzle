package org.zhenchao.nozzle.source;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import org.zhenchao.nozzle.BaseTestCase;

public class SourceProviderFactoryTest extends BaseTestCase {

    @Test
    public void testGetInstance() {
        assertNotNull(SourceProviderFactory.getInstance());
        assertSame(SourceProviderFactory.getInstance(), SourceProviderFactory.getInstance());
    }

}
