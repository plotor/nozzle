package org.zhenchao.nozzle.core;

import org.zhenchao.nozzle.Attribute;
import org.zhenchao.nozzle.Configurable;

import java.io.File;
import java.util.Properties;
import javax.annotation.PostConstruct;

@Configurable
public class ConfigurableObject {
    @Attribute(defaultValue = "1780000")
    long longValue;
    @Attribute(name = "another.long.value", defaultValue = "1000000")
    long anotherLongValue;
    float anotherFloat;
    @Attribute(name = "myFiles")
    private File[] files;
    @Attribute(defaultValue = "15")
    private int number;
    @Attribute(name = "property.message")
    private String propMessage;
    private Double floatingPointNumber;
    @Attribute
    private String fieldMessage;

    /*@Attribute
    private List<String> list;

    @Attribute
    private Set<String> set;*/
    @Attribute
    private Boolean trueFalse;
    @Attribute(raw = true)
    private Properties properties;
    private char aCharacter;

    private byte aByte;

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

    @Attribute
    public Double getFloatingPointNumber() {
        return this.floatingPointNumber;
    }

    public String getMessage() {
        return this.helloWorld;
    }

    public String getPropMessage() {
        return this.propMessage;
    }

    public void setAnotherLongValue(long anotherLongValue) {
        this.anotherLongValue = anotherLongValue;
    }

    @Attribute
    public void setName(String name) {
        this.helloWorld = "Hello " + name + "!";
    }

    public String getFieldMessage() {
        return this.fieldMessage;
    }

    @Attribute
    public char getaCharacter() {
        return this.aCharacter;
    }

    public void setaCharacter(char aCharacter) {
        this.aCharacter = aCharacter;
    }

    @Attribute(name = "oneMoreFloat")
    public void setAnotherFloat(float anotherFloat) {
        this.anotherFloat = anotherFloat;
    }

    public byte getMyByte() {
        return this.aByte;
    }

    @Attribute(name = "aByte", defaultValue = "1")
    public void setMyByte(byte aByte) {
        this.aByte = aByte;
    }

    public double getBigNum() {
        return this.bigNum;
    }

    public Boolean getTrueFalse() {
        return this.trueFalse;
    }

    public void setTrueFalse(Boolean trueFalse) {
        this.trueFalse = trueFalse;
    }

    @PostConstruct
    public void add() {
        this.bigNum = this.number + this.floatingPointNumber;
    }

    /*public List<String> getList() {
        return list;
    }

    public ConfigurableObject setList(List<String> list) {
        this.list = list;
        return this;
    }

    public Set<String> getSet() {
        return set;
    }

    public ConfigurableObject setSet(Set<String> set) {
        this.set = set;
        return this;
    }*/

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}