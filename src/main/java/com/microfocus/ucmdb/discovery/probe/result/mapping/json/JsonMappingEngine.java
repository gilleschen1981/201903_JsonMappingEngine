package com.microfocus.ucmdb.discovery.probe.result.mapping.json;

import appilog.common.system.types.ObjectStateHolder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.microfocus.ucmdb.discovery.probe.result.mapping.json.config.CIConfig;
import com.microfocus.ucmdb.discovery.probe.result.mapping.json.config.JsonMappingConfig;
import com.microfocus.ucmdb.discovery.probe.result.mapping.json.config.RelationConfig;
import com.microfocus.ucmdb.discovery.probe.result.mapping.json.model.ResultHolder;
import com.microfocus.ucmdb.discovery.probe.result.mapping.json.util.JsonMappingEngineUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.Configuration.defaultConfiguration;

public class JsonMappingEngine {
    final Logger logger = Logger.getLogger(JsonMappingEngine.class);
    final static private String RELATION_COMPOSITION = "composition";
    private JsonMappingConfig mappingConfig = null;


    public JsonMappingConfig getMappingConfig() {
        if(mappingConfig == null){
            mappingConfig = new JsonMappingConfig();
        }
        return mappingConfig;
    }

    public void setMappingConfig(JsonMappingConfig mappingConfig) {
        this.mappingConfig = mappingConfig;
    }

    public ResultHolder convert(String json){
        ResultHolder rlt = new ResultHolder();
        // check json format
        DocumentContext valueDoc = null;
        DocumentContext pathDoc = null;
        try {
            valueDoc = JsonPath.using(defaultConfiguration()).parse(json);
            pathDoc = JsonPath.using(defaultConfiguration().addOptions(Option.AS_PATH_LIST)).parse(json);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[LOAD]Can parse the json: " + json);
        }
        if(pathDoc == null || valueDoc == null){
            logger.error("[LOAD]Parse the json error: " + json);
            return null;
        }

        // transform ci
        transformComposition(pathDoc, valueDoc, rlt);
        transformCIs(pathDoc, valueDoc, rlt);
        transformRelation(rlt);
        return rlt;
    }

    private void transformComposition(DocumentContext pathDoc, DocumentContext valueDoc, ResultHolder rlt) {
        Iterator<Map.Entry<String, RelationConfig>> iter = mappingConfig.getRelations().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, RelationConfig> entry = iter.next();
            if(RELATION_COMPOSITION.equals(entry.getValue().getRelationType())){
                String end1 = entry.getValue().getEnd1Id();
                String end2 = entry.getValue().getEnd2Id();
                transformCI(pathDoc, valueDoc, rlt, end1, null);
                transformCI(pathDoc, valueDoc, rlt, end2, end1);

                // remove if from config
                mappingConfig.getCIs().remove(end1);
                mappingConfig.getCIs().remove(end2);
                iter.remove();

            }
        }
    }


    private void transformRelation(ResultHolder rlt) {
        for(Map.Entry<String, RelationConfig> entry : mappingConfig.getRelations().entrySet()){
            String relationType = entry.getValue().getRelationType();
            String end1 = entry.getValue().getEnd1Id();
            String end2 = entry.getValue().getEnd2Id();

            Map<String, ObjectStateHolder> end1Map = rlt.getPathMap().get(end1);
            Map<String, ObjectStateHolder> end2Map = rlt.getPathMap().get(end2);
            if(end1Map == null || end2Map == null){
                continue;
            }

            for(String rootPath1 : end1Map.keySet()){
                for(String rootPath2 : end2Map.keySet()){
                    if(rootPath2!= null && rootPath2.startsWith(rootPath1)){
                        // add relation
                        createLink(relationType, end1Map.get(rootPath1), end2Map.get(rootPath2), rlt);
                    }
                }
            }
        }

    }


    private void createLink(String relationType, ObjectStateHolder end1, ObjectStateHolder end2, ResultHolder rlt) {
        if(relationType != "composition"){
            ObjectStateHolder link = new ObjectStateHolder(relationType);
            link.setAttribute("link_end1", end1);
            link.setAttribute("link_end2", end2);
            rlt.getOshv().add(link);
        } else{
            end2.setContainer(end1);
        }
    }

    private void transformCIs(DocumentContext pathDoc, DocumentContext valueDoc, ResultHolder rlt) {
        for(Map.Entry<String, CIConfig> entry : getMappingConfig().getCIs().entrySet()){
            transformCI(pathDoc, valueDoc, rlt, entry.getKey(), null);
        }
    }

    private void transformCI(DocumentContext pathDoc, DocumentContext valueDoc, ResultHolder rlt, String CIId, String compositionEnd1Id) {
        CIConfig ci = mappingConfig.getCIs().get(CIId);
        if(ci == null){
            return;
        }

        Map<String, ObjectStateHolder> parentCIMap = null;
        if(compositionEnd1Id != null && !"".equals(compositionEnd1Id)){
            parentCIMap = rlt.getPathMap().get(compositionEnd1Id);
        }

        Map<String, List<String>> attributesMap = new HashMap<String, List<String>>();
        Map<String, String> staticAttributeMap = new HashMap<String, String>();
        int ciCount = 0;
        for(String attrName : ci.getAttributes().keySet()){
            if(JsonMappingEngineUtil.isLocator(ci.getAttributes().get(attrName))){
//                    List<String> valueLocatorList = JsonPath.using(Configuration.builder()
//                            .options(Option.AS_PATH_LIST).build()).parse(json).read(ci.getAttributes().get(attrName));
                List<String> valueLocatorList = pathDoc.read(ci.getAttributes().get(attrName));
                int c = valueLocatorList == null ? 0 : valueLocatorList.size();
                if(ciCount == 0){
                    ciCount = valueLocatorList == null ? 0 : valueLocatorList.size();
                }
                if(ciCount != c){
                    logger.error("[PARSE]attribute number don't match. "
                            + attrName + " has " + c + " hit in json while "
                            + ciCount + " is expected.");
                    return ;
                }
                attributesMap.put(attrName, valueLocatorList);
            } else{
                staticAttributeMap.put(attrName, ci.getAttributes().get(attrName));
            }

        }

        for(int i = 0; i < ciCount ; i++){
            ObjectStateHolder osh = new ObjectStateHolder(ci.getClassType());
            String pathRoot = "";
            for(String attrName : attributesMap.keySet()){
                String locator = attributesMap.get(attrName).get(i);

                String pr = JsonMappingEngineUtil.calculatePathRoot(locator);
                if("".equals(pathRoot)){
                    pathRoot = pr;
                }
                if(!pathRoot.equals(pr)){
                    logger.error("[PARSE]attribute pathroot don't match. "
                            + attrName + " is " + pr + " while "
                            + pathRoot + " is expected.");
                    return ;
                }
                osh.setAttribute(attrName, valueDoc.read(locator));

            }
            for(String attrName : staticAttributeMap.keySet()){
                osh.setAttribute(attrName, staticAttributeMap.get(attrName));
            }

            // deal with composition link
            if(parentCIMap != null){
                for(String parentPathRoot : parentCIMap.keySet()){
                    if(pathRoot.startsWith(parentPathRoot)){
                        osh.setContainer(parentCIMap.get(parentPathRoot));
                    }
                }
            }

            rlt.addObjectHolder(CIId, osh, pathRoot);
        }
    }

    public static void main(String args[]) {
        File file = new File("data\\instance.json");
        String json = "";
        try {
            FileInputStream in=new FileInputStream(file);
            int size=in.available();
            byte[] buffer=new byte[size];

            in.read(buffer);
            in.close();
            json=new String(buffer,"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

//        JsonMappingEngine engine = new JsonMappingEngine();
//        engine.convert(json);



        // load json with JsonPath'
        List<String> rlt1 = null;

        Configuration conf = Configuration.builder()
                .options(Option.AS_PATH_LIST).build();
//        rlt1 = JsonPath.using(conf).parse(json).read("$.Reservations[*].Instances[*].InstanceId");
//        System.out.println(rlt1);
//
//        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
//        for(String s : rlt1){
//            System.out.println((String) JsonPath.read(document, s));
//        }


        Object jsonObject = defaultConfiguration().jsonProvider().parse(json);
        DocumentContext newContext = JsonPath.using(defaultConfiguration().addOptions(Option.AS_PATH_LIST)).parse(json);


        System.out.println(newContext.read("$.Reservations[*].Instances[*].InstanceId").toString());



    }
}
