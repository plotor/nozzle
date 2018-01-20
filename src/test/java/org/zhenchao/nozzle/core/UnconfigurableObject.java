package org.zhenchao.nozzle.core;

import org.zhenchao.nozzle.Attribute;

import java.io.File;
import javax.annotation.PostConstruct;

public class UnconfigurableObject {
    @Attribute(name = "myFiles")
    private File[] files;

    @Attribute(defaultValue = "15")
    private int number;

    @Attribute
    private Double floatingPointNumber;

    private String helloWorld;

    private double bigNum = 0;

    public File[] getFiles() {
        return this.files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Double getFloatingPointNumber() {
        return this.floatingPointNumber;
    }

    public void setFloatingPointNumber(Double floatingPointNumber) {
        this.floatingPointNumber = floatingPointNumber;
    }

    public String getMessage() {
        return this.helloWorld;
    }

    @Attribute
    public void setName(String name) {
        this.helloWorld = "Hello " + name + "!";
    }

    public double getBigNum() {
        return this.bigNum;
    }

    @PostConstruct
    public void add() {
        this.bigNum = this.number + this.floatingPointNumber;
    }
}
