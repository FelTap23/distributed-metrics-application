package com.tapiax.metrics.controllers;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.tapiax.metrics.service.ServiceQueryStateStore;

@RestController
public class ControllerAPI {
	
	@Autowired
	private ServiceQueryStateStore serviceQueryStateStore;

	@GetMapping("/metrics/{productId}/{timeZone}")
	public ResponseEntity<String> metricsPerDay(@PathVariable String productId, @PathVariable String timeZone) {
		ZoneId zoneId = TimeZone.getTimeZone(timeZone).toZoneId();
		Optional<JSONObject> optional = serviceQueryStateStore.checkPurchasesPerDayOfSpecificProduct(productId, zoneId);
		if(optional.isPresent()) {
			return new ResponseEntity<>(optional.get().toString(), HttpStatus.OK);
			
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/metrics-all/{timeZone}")
	public List<JSONObject> allMetrics(@PathVariable String timeZone){
		ZoneId zoneId = TimeZone.getTimeZone(timeZone).toZoneId();
		return  serviceQueryStateStore.checkPurchasesPerDay(zoneId);
	}

}
