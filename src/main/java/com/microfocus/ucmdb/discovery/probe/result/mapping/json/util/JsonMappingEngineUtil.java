package com.microfocus.ucmdb.discovery.probe.result.mapping.json.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonMappingEngineUtil {
    public static String calculatePathRoot(String locator) {
        // give "$['Reservations'][0]['Instances'][0]['InstanceId']"
        // return "$['Reservations'][0]['Instances'][0]"
        // to locate the last array index

        Pattern arrayIndexPattern = Pattern.compile("\\[\\d+\\]");
        Matcher m = arrayIndexPattern.matcher(locator);

        int endIndex = 0;
        while(m.find()){
            endIndex = m.end();
        }

        return locator.substring(0,endIndex);
    }

    public static boolean isLocator(String value){
        if(value != null && value.startsWith("$")){
            return true;
        } else{
            return false;
        }
    }
}
