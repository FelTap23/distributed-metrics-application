package com.tapiax.metrics.persistence;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowStore;

public  class Storage {

	public static <K,V>  Materialized<K, V, WindowStore<Bytes, byte[]>> timeWindowStore(String stateStoreName,  Duration retentionTime,    Duration windowSize,  Serde<K> keySerde, Serde<V> valueSerde){
		Materialized<K, V, WindowStore<Bytes, byte[]>> materializedStateStore = Materialized.as(Stores.persistentWindowStore(stateStoreName, retentionTime, windowSize, false));
		materializedStateStore.withKeySerde(keySerde);
		materializedStateStore.withValueSerde(valueSerde);
		return materializedStateStore;
	}
	
	public static <K,V> Materialized<K, V, WindowStore<Bytes, byte[]>> timeWindowStoreInMemory(String stateStoreName, Duration retentionPeriod, Duration windowSize, Serde<K> keySerde, Serde<V> valueSerde){
		Materialized<K, V, WindowStore<Bytes, byte[]>> materializedStateStore =  Materialized.as(Stores.inMemoryWindowStore(stateStoreName, retentionPeriod, windowSize, false));
		materializedStateStore.withKeySerde(keySerde);
		materializedStateStore.withValueSerde(valueSerde);
		return materializedStateStore;
	}
	
	public static <K,V> Materialized<K, V, KeyValueStore<Bytes, byte[]>> keyValueStoreMaterialized(String stateStoreName, Serde<K> keySerde, Serde<V> valueSerde){
		Materialized<K, V, KeyValueStore<Bytes, byte[]>> materializedStateStore =  Materialized.as(Stores.persistentKeyValueStore(stateStoreName));
		materializedStateStore.withKeySerde(keySerde);
		materializedStateStore.withValueSerde(valueSerde);
		return materializedStateStore;
	}
	
	public static <K,V> Materialized<K, V, KeyValueStore<Bytes, byte[]>> keyValueStoreMaterializedInMemory(String stateStoreName, Serde<K> keySerde, Serde<V> valueSerde){
		Materialized<K, V, KeyValueStore<Bytes, byte[]>> materializedStateStore =  Materialized.as(Stores.inMemoryKeyValueStore(stateStoreName));
		materializedStateStore.withKeySerde(keySerde);
		materializedStateStore.withValueSerde(valueSerde);
		return materializedStateStore;
	}
}
