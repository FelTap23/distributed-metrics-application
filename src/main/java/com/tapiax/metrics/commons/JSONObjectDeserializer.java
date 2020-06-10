package com.tapiax.metrics.commons;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.json.JSONObject;


public class JSONObjectDeserializer implements Deserializer<JSONObject> {
    
    private String encoding = "UTF8";

    /**
     * Default constructor needed by Kafka
     */
    public JSONObjectDeserializer() {
    	
    }
    
    
    

    @Override
    public JSONObject deserialize(String topic, byte[] bytes) {
    	
    	
        if (bytes == null)
            return null;

        JSONObject data;
        try {
            data = new JSONObject(new String(bytes, encoding));
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return data;
    }
}