package com.tapiax.metrics.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

import com.tapiax.metrics.kafka.StreamDefinition;

@Service
public class ServiceQueryStateStore {

	@Autowired
	private InteractiveQueryService interactiveQueryService;

	private TimeIntervalQuery makeTimeInterval(ZoneId zoneId) {
		ZonedDateTime now = ZonedDateTime.now(zoneId);
		ZonedDateTime zdt1 = now.truncatedTo(ChronoUnit.DAYS);
		ZonedDateTime zdt2 = zdt1.toLocalDate().atStartOfDay(zoneId);
		Instant from = zdt2.toInstant();
		Instant to = now.toInstant();
		return new TimeIntervalQuery(from, to);
	}
	
	
	
	private static JSONObject createEmptyJsonObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("numberOfSold", 0);
		jsonObject.put("total", 0);
		return jsonObject; 
	}
	
	
	private static JSONObject mergeJSONObject(JSONObject o1, JSONObject o2) {
		o1.put("numberOfSold", o1.getLong("numberOfSold") +  o2.getLong("numberOfSold"));
		o1.put("total", o1.getLong("total") +  o2.getLong("total"));
		return o1;
	}
	
	

	public Optional<JSONObject> checkPurchasesPerDayOfSpecificProduct(String productId, ZoneId zoneId) {

		final TimeIntervalQuery timeIntervalQuery = makeTimeInterval(zoneId);
		ReadOnlyWindowStore<String, JSONObject> windowStore = interactiveQueryService.getQueryableStore(StreamDefinition.stateStoreName, QueryableStoreTypes.windowStore());
		WindowStoreIterator<JSONObject> iterator = windowStore.fetch(productId, timeIntervalQuery.from,timeIntervalQuery.to);
		
		long numberOfSold = 0;
		double total = 0;

		if (!iterator.hasNext())
			return Optional.empty();

		while (iterator.hasNext()) {
			KeyValue<Long, JSONObject> keyValue = iterator.next();
			JSONObject detail = keyValue.value;
			numberOfSold += detail.getLong("numberOfSold");
			total += detail.getLong("total");
		}
		iterator.close();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("productId", productId);
		jsonObject.put("total", total);
		jsonObject.put("numberOfSold", numberOfSold);
		jsonObject.put("from",timeIntervalQuery.from.atZone(zoneId));
		jsonObject.put("to",timeIntervalQuery.to.atZone(zoneId));
		return Optional.of(jsonObject);
	}
	
	
	public List<JSONObject> checkPurchasesPerDay(ZoneId zoneId) {

		final TimeIntervalQuery timeIntervalQuery = makeTimeInterval(zoneId);
		ReadOnlyWindowStore<String, JSONObject> windowStore = interactiveQueryService.getQueryableStore(StreamDefinition.stateStoreName, QueryableStoreTypes.windowStore());
		KeyValueIterator<Windowed<String>, JSONObject> iterator = windowStore.fetchAll(timeIntervalQuery.from,timeIntervalQuery.to);
		Map<String ,JSONObject> countMap = new HashMap< >();

		if (!iterator.hasNext())
			return  new ArrayList<JSONObject>();

		while (iterator.hasNext()) {
			KeyValue<Windowed<String>, JSONObject> keyValue = iterator.next();
			countMap.merge(
					keyValue.key.key(),  
					createEmptyJsonObject(), 
					ServiceQueryStateStore::mergeJSONObject
			);
		}
		iterator.close();
		countMap.forEach((productId,jsonObject) -> jsonObject.put("productId", productId));
		return new ArrayList<JSONObject>(countMap.values());
	}

	private static class TimeIntervalQuery {
		final Instant from;
		final Instant to;

		public TimeIntervalQuery(Instant from, Instant to) {
			this.from = from;
			this.to = to;
		}
	}
}
