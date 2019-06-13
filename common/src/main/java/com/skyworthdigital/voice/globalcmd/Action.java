package com.skyworthdigital.voice.globalcmd;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Action implements Serializable {
    private static final long serialVersionUID = 1L;
    //private String type;
    private String value;
    private String local;
    //private String activity;
    private List<Parameter> para = new ArrayList<Parameter>();


    public List<Parameter> getParameters() {
        return para;
    }

    public void setParameters(List<Parameter> parameters) {
        this.para = parameters;
    }

    public String getValueByKey(String key) {
        if (key == null || key.length() <= 0) {
            return "";
        }
        for (Parameter param : para) {
            if (key.equals(param.getKey())) {
                return param.getValue();
            }
        }
        return "";
    }

    public String getValue() {
        return value;
    }

    public String getLocal() {
        return local;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "value:" + value + "|local:" + local + "|parameters:" + para;
    }
}
