package com.tapiax.metrics.kafka;

import static com.tapiax.metrics.persistence.Storage.timeWindowStore;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;

import com.tapiax.metrics.bindings.StreamProcess;
import com.tapiax.metrics.commons.JSONObjectSerde;


@EnableBinding(StreamProcess.class)
public class StreamDefinition {
	
	private static Logger logger = LoggerFactory.getLogger(StreamDefinition.class);
	public static final String stateStoreName = "state-store-metrics";
	
	@Autowired
	private Schema schema;
	
	@StreamListener
	@SendTo(StreamProcess.output)
	public KStream<String, JSONObject> process(@Input(StreamProcess.input) KStream<String, JSONObject> inputProcess) {
		
		final Duration windowSize = Duration.ofHours(1);
		final Duration retentionTime = Duration.ofDays(1);
		
		return inputProcess
				.filter( (key,value) -> 
					{
						try {
							schema.validate(value);
							return true;
						}
						catch(ValidationException validationException) {
							logger.error(String.format("%s", validationException.toString()));
							return false;
						}
					} )
				.groupBy( (key,value) ->  (String)value.get("productId"))
				.windowedBy(TimeWindows.of(windowSize))
				.aggregate( () -> {
					JSONObject aggregateNode =  new JSONObject();
					aggregateNode.put("total", 0);
					aggregateNode.put("numberOfSold", 0);
					return aggregateNode; 
					}
					,(key, entryValue, aggregateValue) -> {
					
					
					double total  = aggregateValue.getDouble("total");
					long quantity = entryValue.getLong("quantity");
					total+= entryValue.getDouble("price") * (double)quantity ;
					
					aggregateValue.put("total", total);
					aggregateValue.put("numberOfSold",  aggregateValue.getLong("numberOfSold") +  quantity);
					return aggregateValue;	
					}, timeWindowStore(stateStoreName, retentionTime, windowSize, Serdes.String(), new JSONObjectSerde()))
				.toStream()
				.map( (key,value) ->  { 
					value.put("productId", key.key());
					value.put("start",key.window().start());
					value.put("end",key.window().end());
					return new KeyValue<String,JSONObject>(key.key(), value);
				} );
	}
}
