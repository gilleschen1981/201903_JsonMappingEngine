package com.microfocus.ucmdb.discovery.probe.result.mapping.json.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonMappingConfig implements Serializable {

    // <id, CI> id here is used as the identiffer for this specific CI in the whole configuration context.
    // relationConfig would use this id to refer to all CI generated from this config.
    private Map<String, CIConfig> CIs;

    // <id, relation> id is also the identifer inside this configuration file.
    private Map<String, RelationConfig> relations;

    public Map<String, CIConfig> getCIs() {
        if(CIs == null){
            CIs = new HashMap<String, CIConfig>();
        }
        return CIs;
    }

    public void setCIs(Map<String, CIConfig> CIs) {
        this.CIs = CIs;
    }

    public Map<String, RelationConfig> getRelations() {
        if(relations == null){
            relations = new HashMap<String, RelationConfig>();
        }
        return relations;
    }

    public void setRelations(Map<String, RelationConfig> relations) {
        this.relations = relations;
    }


}
