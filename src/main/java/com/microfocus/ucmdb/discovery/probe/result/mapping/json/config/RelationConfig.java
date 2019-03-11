package com.microfocus.ucmdb.discovery.probe.result.mapping.json.config;

import java.io.Serializable;

public class RelationConfig implements Serializable {
    private String relationType;
    private String end1Id;
    private String end2Id;
    private String condition;

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getEnd1Id() {
        return end1Id;
    }

    public void setEnd1Id(String end1Id) {
        this.end1Id = end1Id;
    }

    public String getEnd2Id() {
        return end2Id;
    }

    public void setEnd2Id(String end2Id) {
        this.end2Id = end2Id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
