package org.zhenchao.nozzle.source.provider;

import static java.lang.String.format;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.zhenchao.nozzle.core.ConfigurationInjector;
import org.zhenchao.nozzle.core.Environment;
import org.zhenchao.nozzle.core.EnvironmentAccessor;
import org.zhenchao.nozzle.core.PropertiesBuilder;
import org.zhenchao.nozzle.exception.ConfigurationException;
import org.zhenchao.nozzle.source.Source;
import org.zhenchao.nozzle.source.SourceProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * 基于本地文件系统的配置加载服务
 *
 * @author zhenchao.wang 2017-09-06 14:16:44
 * @version 1.0.0
 */
public class FilesystemSourceProvider extends AbstractSourceProvider {

    public static final String DEFAULT_ROOT_DIR_ENV_VAR = "APP_ROOT";
    public static final String DEFAULT_CONF_DIR = "conf";

    public static final String ROOT_DIR_ENV_OVERRIDE = "config.file.rootDirEnvVar";
    public static final String ROOT_DIR_OVERRIDE = "config.file.rootDir";
    public static final String CONF_DIR_OVERRIDE = "config.file.confDir";

    private static final String PROVIDER_IDENTIFIER = "com.xiaomi.passport.config.filesystem";
    private static FileWatchListener fileWatchListener;
    private static FileAlterationMonitor fileAlterationMonitor;
    private final FilesystemConfiguration filesystemConfiguration;
    private final Object lock = new Object();

    public FilesystemSourceProvider() {
        this(EnvironmentAccessor.getInstance().getEnvironment());
    }

    FilesystemSourceProvider(Environment environment) {
        this.filesystemConfiguration = new FilesystemConfiguration(environment);
    }

    private synchronized static FileWatchListener getFileWatchListener() {
        if (fileWatchListener == null) {
            fileWatchListener = new FileWatchListener(ConfigurationInjector.getInstance());
        }
        return fileWatchListener;
    }

    FilesystemConfiguration getFilesystemConfiguration() {
        return this.filesystemConfiguration;
    }

    @Override
    public String getProviderIdentifier() {
        return PROVIDER_IDENTIFIER;
    }

    @Override
    public SourceProvider.Priority getPriority() {
        return SourceProvider.Priority.MEDIUM;
    }

    @Override
    public boolean support(Source source) {
        try {
            File file = getConfigurationFile(source.getInjectClass(), getResourceName(source));
            return file.exists();
        } catch (ConfigurationException exc) {
            return false;
        }
    }

    @Override
    public void afterInit() {
        super.afterInit();
        synchronized (lock) {
            if (fileAlterationMonitor == null) {
                fileWatchListener = new FileWatchListener(ConfigurationInjector.getInstance());
                fileAlterationMonitor = new FileAlterationMonitor();
                File confDir = getFilesystemConfiguration().getConfigurationDirectory();
                FileAlterationObserver observer = new FileAlterationObserver(confDir);

                observer.addListener(getFileWatchListener());
                fileAlterationMonitor.addObserver(observer);

                try {
                    fileAlterationMonitor.start();
                } catch (Exception exc) {
                    log.error("Could not start file monitor", exc);
                }
            }
        }
    }

    @Override
    public void beforeDestroy() {
        super.beforeDestroy();
        synchronized (lock) {
            try {
                if (fileAlterationMonitor != null) {
                    fileAlterationMonitor.stop(10);
                }
                if (fileWatchListener != null) {
                    fileWatchListener.clear();
                }

                fileAlterationMonitor = null;
                fileWatchListener = null;
            } catch (Exception exc) {
                log.error("Could not stop file monitor", exc);
            }
        }
    }

    @Override
    public void postHandle(Source source) {
        super.postHandle(source);

        Class<?> referenceClass = source.getInjectClass();
        String resourceName = getResourceName(source);

        try {
            File confFile = this.getConfigurationFile(referenceClass, resourceName);
            getFileWatchListener().addFile(confFile, source);
        } catch (ConfigurationException exc) {
            log.warn("Could not add file to watch list: " + exc.getMessage());
            if (log.isTraceEnabled()) {
                log.trace(exc.getMessage(), exc);
            }
        }
    }

    @Override
    protected Properties createProperties(
            Class<?> injectClass, String resourceName, PropertiesBuilder builder) throws ConfigurationException {
        File file = this.getConfigurationFile(injectClass, resourceName);
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file), 4096);
            Properties props = new Properties();
            props.load(in);
            builder.addAll(props);
            return builder.build();
        } catch (Throwable t) {
            throw new ConfigurationException(format("Could not read configuration for %s using reference class %s", resourceName, injectClass), t);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public Collection<File> getMonitoredFiles() {
        return getFileWatchListener().getMappedFiles();
    }

    public String getMonitoredConfigurationDirectory() {
        try {
            return getFilesystemConfiguration().getConfigurationDirectory().getAbsolutePath();
        } catch (IllegalArgumentException exc) {
            log.warn(format("Could not determine configuration directory: %s", exc.getMessage()));
            if (log.isTraceEnabled()) {
                log.trace(exc.getMessage(), exc);
            }
        }
        return null;
    }

    private File getConfigurationFile(Class<?> injectClass, String resourceName) throws ConfigurationException {
        Pattern pattern = Pattern.compile(String.format("^%s(\\.properties)?(\\.xml)?$", resourceName), Pattern.CASE_INSENSITIVE);
        File file;
        try {
            File confDir = this.getFilesystemConfiguration().getConfigurationDirectory();
            file = this.getConfigurationFileInSubDirs(confDir, injectClass, pattern);

            if (file == null) {
                file = this.getFromPattern(confDir, pattern);
            }
            if (file == null) {
                throw new ConfigurationException(format("Could not find file for %s:%s", injectClass, resourceName));
            }
        } catch (IllegalArgumentException exc) {
            throw new ConfigurationException(format("Could not find file for %s:%s", injectClass, resourceName), exc);
        }
        return file;
    }

    private File getConfigurationFileInSubDirs(File confDir, Class<?> referenceClass, Pattern pattern) {
        String path = referenceClass.getPackage().getName().replaceAll("\\.", File.separator);

        File filePath = new File(confDir, path);
        if (!filePath.isDirectory()) {
            if (log.isDebugEnabled()) {
                log.debug(format("Could not find directory %s", filePath));
            }
            return null;
        }

        return getFromPattern(filePath, pattern);
    }

    private File getFromPattern(File directory, Pattern pattern) {
        File[] match = directory.listFiles(new PatternFileFilter(pattern));

        return ArrayUtils.isNotEmpty(match) ? match[0] : null;
    }

    private static class PatternFileFilter implements FileFilter {
        private final Pattern pattern;

        PatternFileFilter(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean accept(File pathName) {
            return pattern.matcher(pathName.getName()).matches();
        }
    }

    public static class FilesystemConfiguration {

        private final Environment environment;

        private final String confDir;
        private final String rootDirEnvironmentVar;
        private final String rootDir;

        FilesystemConfiguration(Environment environment) {
            this.environment = environment;
            this.confDir = this.environment.getSystemProperty(CONF_DIR_OVERRIDE, DEFAULT_CONF_DIR);
            this.rootDirEnvironmentVar = this.environment.getSystemProperty(ROOT_DIR_ENV_OVERRIDE, DEFAULT_ROOT_DIR_ENV_VAR);
            this.rootDir = this.environment.getSystemProperty(ROOT_DIR_OVERRIDE, null);
        }

        Environment getEnvironment() {
            return this.environment;
        }

        String getRootDir() {
            return this.rootDir;
        }

        String getRootDirEnvironmentVar() {
            return this.rootDirEnvironmentVar;
        }

        String getConfDir() {
            return this.confDir;
        }

        public File getConfigurationDirectory() {
            File rootDir;
            if (StringUtils.isNotBlank(this.getRootDir())) {
                rootDir = new File(this.getRootDir());
            } else if (StringUtils.isNotBlank(getRootDirEnvironmentVar())) {
                String rootDirEnvVar = this.getEnvironment().getEnvVariable(this.getRootDirEnvironmentVar());
                if (StringUtils.isBlank(rootDirEnvVar)) {
                    throw new IllegalArgumentException(
                            format("There is no value for the environment variable '%s'.", getRootDirEnvironmentVar()));
                }
                rootDir = new File(rootDirEnvVar);
            } else {
                throw new IllegalArgumentException(
                        format("There is neither an value set for the environment variable '%s', nor has a root directory been set via the system override.",
                                getRootDirEnvironmentVar()));
            }

            File confDir = rootDir;
            if (StringUtils.isNotBlank(getConfDir())) {
                confDir = new File(rootDir, getConfDir());
            }

            if (!confDir.exists()) {
                throw new IllegalArgumentException(format("Cannot find the directory '%s'.", confDir));
            }
            return confDir;
        }
    }

    /**
     * @author zhenchao.wang 2016-09-06 14:23:46
     * @version 1.0.0
     */
    public static class FileWatchListener implements FileAlterationListener {
        private final Map<File, Source> mapper;
        private final ConfigurationInjector configurationUtilities;

        FileWatchListener(ConfigurationInjector configurationUtilities) {
            this.mapper = new HashMap<File, Source>();
            this.configurationUtilities = configurationUtilities;
        }

        public void clear() {
            synchronized (this.mapper) {
                this.mapper.clear();
            }
        }

        public void addFile(File file, Source source) {
            if (file == null) {
                throw new IllegalArgumentException("There was no file provided to the file watch listener");
            }
            if (source == null) {
                throw new IllegalArgumentException(format("The file %s was not provided a configuration source identifier", file.getAbsoluteFile()));
            }

            synchronized (this.mapper) {
                this.mapper.put(file, source);
            }
        }

        @SuppressWarnings("unchecked")
        public Collection<File> getMappedFiles() {
            synchronized (this.mapper) {
                return CollectionUtils.unmodifiableCollection(this.mapper.keySet());
            }
        }

        @Override
        public void onStart(FileAlterationObserver fileAlterationObserver) { /* ignore */ }

        @Override
        public void onDirectoryCreate(File file) { /* ignore */ }

        @Override
        public void onDirectoryChange(File file) { /* ignore */ }

        @Override
        public void onDirectoryDelete(File file) { /* ignore */ }

        @Override
        public void onFileCreate(File file) { /* ignore */ }

        @Override
        public void onFileChange(File file) {
            resetProperties(file);
        }

        @Override
        public void onFileDelete(File file) {
            synchronized (this.mapper) {
                if (this.mapper.containsKey(file)) {
                    log.info(format("The file '%s' has been deleted", file));
                    this.mapper.remove(file);
                }
            }
        }

        @Override
        public void onStop(FileAlterationObserver fileAlterationObserver) { /* ignore */ }

        private void resetProperties(File file) {
            if (this.mapper.containsKey(file)) {
                try {
                    this.configurationUtilities.reload(this.mapper.get(file));
                } catch (ConfigurationException exc) {
                    log.warn(format("Could not reset properties on the file %s", file), exc);
                }
            }
        }
    }
}
