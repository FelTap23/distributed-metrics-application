package com.tapiax.metrics.commons;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.json.JSONObject;

public class JSONObjectSerializer implements Serializer<JSONObject> {

	private String encoding = "UTF8";

	@Override
	public byte[] serialize(String topic, JSONObject data) {

		if (data == null)
			return null;

		byte[] bytes = null;

		try {
			bytes = data.toString().getBytes(encoding);
		} catch (Exception e) {
			throw new SerializationException(e);
		}

		return bytes;
	}

}
