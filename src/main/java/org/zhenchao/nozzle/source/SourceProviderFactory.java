package org.zhenchao.nozzle.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhenchao.nozzle.source.provider.ClasspathSourceProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * {@link SourceProvider} factory
 *
 * @author zhenchao.wang 2016-09-06 11:39:06
 * @version 1.0.0
 */
public class SourceProviderFactory {

    private static final Logger log = LoggerFactory.getLogger(SourceProviderFactory.class);

    private static final SourceProviderFactory INSTANCE = new SourceProviderFactory();

    private final Map<Source, SourceProvider> providerMap;
    private final Set<SourceProvider> initializedProviders;
    private final ServiceLoader<SourceProvider> serviceLoader;

    private SourceProviderFactory() {
        this.providerMap = new HashMap<Source, SourceProvider>();
        this.initializedProviders = new HashSet<SourceProvider>();
        this.serviceLoader = ServiceLoader.load(SourceProvider.class, Thread.currentThread().getContextClassLoader());
    }

    public static SourceProviderFactory getInstance() {
        return INSTANCE;
    }

    public SourceProvider getSourceProvider(Source source) {
        this.mapSourceIdentifier(source);
        return providerMap.get(source);
    }

    public void clearAssociations() {
        for (SourceProvider provider : providerMap.values()) {
            if (initializedProviders.contains(provider)) {
                provider.beforeDestroy();
            }
        }
        initializedProviders.clear();
        providerMap.clear();
    }

    private void mapSourceIdentifier(Source source) {
        if (providerMap.get(source) != null) {
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("Mapping provider for %s", source));
        }

        Iterator<SourceProvider> providers = this.getSourceProviders();

        SourceProvider chosenProvider = new ClasspathSourceProvider();
        while (providers.hasNext()) {
            SourceProvider provider = providers.next();
            if (provider.support(source) && chosenProvider.getPriority().getValue() < provider.getPriority().getValue()) {
                chosenProvider = provider;
            }
        }
        providerMap.put(source, chosenProvider);

        if (!initializedProviders.contains(chosenProvider)) {
            chosenProvider.afterInit();
            initializedProviders.add(chosenProvider);
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format("Mapped the provider %s to the identifier %s", chosenProvider.getProviderIdentifier(), source));
        }
    }

    private Iterator<SourceProvider> getSourceProviders() {
        serviceLoader.reload();
        return serviceLoader.iterator();
    }

}
