package org.zhenchao.nozzle.source;

import org.zhenchao.nozzle.core.MockConfigurableObject;
import org.zhenchao.nozzle.core.PropertiesBuilderFactory;

import java.util.Properties;

public class MockConfSourceProvider implements SourceProvider {

    private static final String ID = "MOCK-PROVIDER";

    public String getProviderIdentifier() {
        return ID;
    }

    @Override
    public Priority getPriority() {
        return Priority.HIGH;
    }

    public Properties getProperties(Source source, PropertiesBuilderFactory builderFactory) {
        Properties props = new Properties();
        props.setProperty("myFiles", "/tmp/file.txt, /tmp/text.txt");
        props.setProperty("number", "23");
        props.setProperty("floatingPointNumber", "123.56");
        props.setProperty("trueFalse", "false");
        props.setProperty("name", "Z-Carioca");
        props.setProperty("oneMoreFloat", "1.34");
        props.setProperty("aByte", "120");
        props.setProperty("fieldMessage", "This is a simple message");
        props.setProperty("aCharacter", "s");
        props.setProperty("property.message", "This is a simple property message");
        props.setProperty("another.long.value", "500");

        return props;
    }

    public boolean support(Source source) {
        return source.equals(new Source(new MockConfigurableObject()));
    }

    public void afterInit() {
    }

    public void beforeDestroy() {
    }
}
