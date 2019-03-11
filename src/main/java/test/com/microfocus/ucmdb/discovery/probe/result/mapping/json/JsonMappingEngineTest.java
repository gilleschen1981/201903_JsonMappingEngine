package test.com.microfocus.ucmdb.discovery.probe.result.mapping.json; 

import com.microfocus.ucmdb.discovery.probe.result.mapping.json.JsonMappingEngine;
import com.microfocus.ucmdb.discovery.probe.result.mapping.json.config.JsonMappingConfigLoader;
import com.microfocus.ucmdb.discovery.probe.result.mapping.json.model.ResultHolder;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/** 
* JsonMappingEngine Tester. 
* 
* @author <Authors name> 
* @since <pre>Feb 16, 2019</pre> 
* @version 1.0 
*/ 
public class JsonMappingEngineTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: convert(String json) 
* 
*/ 
@Test
public void testConvert() throws Exception {
    JsonMappingEngine engine = new JsonMappingEngine();
    engine.setMappingConfig(JsonMappingConfigLoader.loadJson(new File("data\\config")));

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

    ResultHolder r = engine.convert(json);
    System.out.println(r.generateVector());
} 

/** 
* 
* Method: main(String args[]) 
* 
*/ 
@Test
public void testMain() throws Exception { 
//TODO: Test goes here... 
} 


} 
