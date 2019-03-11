package com.microfocus.ucmdb.discovery.probe.result.mapping.json.model;

import appilog.common.system.types.ObjectStateHolder;
import appilog.common.system.types.vectors.ObjectStateHolderVector;

import java.util.HashMap;
import java.util.Map;

public class ResultHolder {
    private ObjectStateHolderVector oshv ;

    // <CIId, <jpath, pointer to the CIObjectHolder>>
    Map<String, Map<String, ObjectStateHolder>> pathMap;

    public ObjectStateHolderVector getOshv() {
        if(oshv == null){
            oshv = new ObjectStateHolderVector();
        }
        return oshv;
    }

    public Map<String, Map<String, ObjectStateHolder>> getPathMap() {
        if(pathMap == null){
            pathMap = new HashMap<String, Map<String, ObjectStateHolder>>();
        }
        return pathMap;
    }

    public void addObjectHolder(String CIId, ObjectStateHolder osh, String pathRoot) {
        if(oshv == null){
            oshv = new ObjectStateHolderVector();
        }
        oshv.add(osh);
        addPathMap(CIId, pathRoot, osh);
    }

    private void addPathMap(String ciId, String pathRoot, ObjectStateHolder osh) {
        if(pathMap == null){
            pathMap = new HashMap<String, Map<String, ObjectStateHolder>>();
        }
        Map<String, ObjectStateHolder> objectMap = pathMap.getOrDefault(ciId, new HashMap<String, ObjectStateHolder>());
        objectMap.put(pathRoot, osh);
        pathMap.put(ciId, objectMap);
    }

    public String generateVector() {
        return oshv.toXmlString();
    }
}
