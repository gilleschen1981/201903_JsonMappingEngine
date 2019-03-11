package com.microfocus.ucmdb.discovery.probe.result.mapping.json.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonMappingConfigLoader {
    public static JsonMappingConfig loadJson(File file){
        JsonMappingConfig rlt = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            rlt = mapper.readValue(file, JsonMappingConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rlt;
    }

    public static void writeJson(JsonMappingConfig config, File file){
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        try {
            mapper.writeValue(file, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
