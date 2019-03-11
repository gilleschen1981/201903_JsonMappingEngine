package com.microfocus.ucmdb.discovery.probe.result.mapping.json.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CIConfig implements Serializable {
    private String classType;

    private Map<String, String> attributes;

    private Map<String, String> attributesType;

    private Map<String, String> attributeTransform;

    private String condition;

    public Map<String, String> getAttributesType() {
        if(attributesType == null){
            attributesType = new HashMap<String, String>();
        }
        return attributesType;
    }

    public void setAttributesType(Map<String, String> attributesType) {
        this.attributesType = attributesType;
    }

    public String getCondition() {
        return condition;
    }

    public Map<String, String> getAttributeTransform() {
        if(attributeTransform == null){
            attributeTransform = new HashMap<String, String>();
        }
        return attributeTransform;
    }

    public void setAttributeTransform(Map<String, String> attributeTransform) {
        this.attributeTransform = attributeTransform;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public Map<String, String> getAttributes() {
        if(attributes == null){
            attributes = new HashMap<String, String>();
        }
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
