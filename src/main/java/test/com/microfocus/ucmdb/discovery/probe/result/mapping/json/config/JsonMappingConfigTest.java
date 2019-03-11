package test.com.microfocus.ucmdb.discovery.probe.result.mapping.json.config; 

import com.microfocus.ucmdb.discovery.probe.result.mapping.json.config.CIConfig;
import com.microfocus.ucmdb.discovery.probe.result.mapping.json.config.JsonMappingConfig;
import com.microfocus.ucmdb.discovery.probe.result.mapping.json.config.JsonMappingConfigLoader;
import com.microfocus.ucmdb.discovery.probe.result.mapping.json.config.RelationConfig;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.io.File;

/** 
* JsonMappingConfig Tester. 
* 
* @author <Authors name> 
* @since <pre>Feb 11, 2019</pre> 
* @version 1.0 
*/ 
public class JsonMappingConfigTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
}

@Test
public void testSaveLoad(){
    JsonMappingConfig config = new JsonMappingConfig();
    CIConfig nodeCi = new CIConfig();
    nodeCi.setClassType("node");
    nodeCi.getAttributes().put("cloud_instance_id", "$.Reservations[*].Instances[*].InstanceId");
    nodeCi.getAttributeTransform().put("cloud_instance_id", "");
    nodeCi.getAttributesType().put("cloud_instance_id", "String");
    nodeCi.getAttributes().put("name", "$.Reservations[*].Instances[*].ImageId");
    nodeCi.getAttributeTransform().put("name", "");
    nodeCi.getAttributesType().put("name", "String");
    config.getCIs().put("node1", nodeCi);

    CIConfig ipCi = new CIConfig();
    ipCi.setClassType("ip_address");
    ipCi.getAttributes().put("name", "$.Reservations[*].Instances[*].PublicIpAddress");
    ipCi.getAttributeTransform().put("name","");
    ipCi.getAttributesType().put("name", "String");
    ipCi.getAttributes().put("ip_lease_time", "0");
    ipCi.getAttributesType().put("ip_lease_time", "String");
    config.getCIs().put("publicIp", ipCi);

    RelationConfig nodeToIp= new RelationConfig();
    nodeToIp.setEnd1Id("node1");
    nodeToIp.setEnd2Id("publicIp");
    nodeToIp.setRelationType("containment");
    config.getRelations().put("nodeToIp", nodeToIp);

    CIConfig diskCi = new CIConfig();
    diskCi.setClassType("disk_device");
    diskCi.getAttributes().put("name", "$.Reservations[*].Instances[*].BlockDeviceMappings[*].DeviceName");
    diskCi.getAttributesType().put("name", "String");
    diskCi.getAttributeTransform().put("name", "");
    config.getCIs().put("disk", diskCi);

    RelationConfig nodeToDisk = new RelationConfig();
    nodeToDisk.setEnd1Id("node1");
    nodeToDisk.setEnd2Id("disk");
    nodeToDisk.setRelationType("composition");
    config.getRelations().put("nodeToDisk", nodeToDisk);

    File file = new File("data\\config");
    JsonMappingConfigLoader.writeJson(config, file);

}
} 
