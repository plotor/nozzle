package org.zhenchao.nozzle.core;

import org.zhenchao.nozzle.Attribute;
import org.zhenchao.nozzle.Configurable;

@Configurable(resource = "test")
public class AnotherConfigurableObject {
    @Attribute(name = "value.1")
    private String firstValue;

    @Attribute(name = "value.2")
    private String secondValue;

    public String getFirstValue() {
        return this.firstValue;
    }

    public String getSecondValue() {
        return this.secondValue;
    }
}