package com.tapiax.metrics.commons;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes.WrapperSerde;
import org.apache.kafka.common.serialization.Serializer;
import org.json.JSONObject;

public class JSONObjectSerde   extends WrapperSerde<JSONObject>{

	public JSONObjectSerde(Serializer<JSONObject> serializer, Deserializer<JSONObject> deserializer) {
		super(serializer, deserializer);
	}
	
	public JSONObjectSerde() {
		super( new JSONObjectSerializer(), new JSONObjectDeserializer());
	}

}
