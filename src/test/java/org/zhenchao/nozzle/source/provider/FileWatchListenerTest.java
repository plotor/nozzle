package org.zhenchao.nozzle.source.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.zhenchao.nozzle.BaseTestCase;
import org.zhenchao.nozzle.core.ConfigurationInjector;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.source.Source;

import java.io.File;
import java.util.Collection;

public class FileWatchListenerTest extends BaseTestCase {
    private ConfigurationUtilitiesMock configurationUtilities;

    @Before
    public void setUp() throws Exception {
        configurationUtilities = new ConfigurationUtilitiesMock();
    }

    @Test
    public void testAddFile() {
        FilesystemSourceProvider.FileWatchListener listener = new FilesystemSourceProvider.FileWatchListener(configurationUtilities);
        listener.addFile(new File("file1.txt"), new Source(this));
        listener.addFile(new File("file2.txt"), new Source(this));

        Collection<File> mappedFiles = listener.getMappedFiles();
        assertEquals(2, mappedFiles.size());
        assertTrue(mappedFiles.contains(new File("file1.txt")));
        assertTrue(mappedFiles.contains(new File("file2.txt")));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testImmutableMappedFiles() {
        FilesystemSourceProvider.FileWatchListener listener = new FilesystemSourceProvider.FileWatchListener(configurationUtilities);
        listener.addFile(new File("file1.txt"), new Source(this));
        listener.addFile(new File("file2.txt"), new Source(this));

        Collection<File> mappedFiles = listener.getMappedFiles();
        mappedFiles.add(new File("file3.txt"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFileNullFile() {
        FilesystemSourceProvider.FileWatchListener listener = new FilesystemSourceProvider.FileWatchListener(configurationUtilities);
        listener.addFile(null, new Source(this));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFileNullCSI() {
        FilesystemSourceProvider.FileWatchListener listener = new FilesystemSourceProvider.FileWatchListener(configurationUtilities);
        listener.addFile(new File("file1.txt"), null);
    }

    @Test
    public void testOnFileChanged() {
        FilesystemSourceProvider.FileWatchListener listener = new FilesystemSourceProvider.FileWatchListener(configurationUtilities);
        listener.onFileChange(new File("file1.txt"));
        assertFalse(configurationUtilities.isRanReconfiguration());

        listener.addFile(new File("file1.txt"), new Source(this));
        listener.onFileChange(new File("file1.txt"));
        assertTrue(configurationUtilities.isRanReconfiguration());
    }

    @Test
    public void testOnFileDeleted() {
        FilesystemSourceProvider.FileWatchListener listener = new FilesystemSourceProvider.FileWatchListener(configurationUtilities);
        listener.addFile(new File("file1.txt"), new Source(this));
        listener.addFile(new File("file2.txt"), new Source(this));
        assertEquals(2, listener.getMappedFiles().size());

        listener.onFileDelete(new File("file1.txt"));
        assertEquals(1, listener.getMappedFiles().size());

        listener.onFileDelete(new File("file1.txt"));
        assertEquals(1, listener.getMappedFiles().size());
    }

    private static class ConfigurationUtilitiesMock extends ConfigurationInjector {

        private boolean ranReconfiguration = false;

        public ConfigurationUtilitiesMock() {
            super();
        }

        public boolean isRanReconfiguration() {
            return this.ranReconfiguration;
        }

        @Override
        public void reload(Source source) throws ConfigurationException {
            ranReconfiguration = true;
        }
    }
}