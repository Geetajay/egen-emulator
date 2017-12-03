package com.egen.sensor;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.HttpHostConnectException;

import groovyx.net.http.HTTPBuilder;

public class Emulator {

private static int interval_in_ms = 5000;
	
	public static void main(String[] args) throws Exception {
		int base_weight;
		try {
			System.out.println("System.getProperty--" + System.getProperty("base.value"));
			base_weight = Integer.parseInt(System.getProperty("base.value"));
		} catch (NumberFormatException e) {
			throw new IllegalStateException("Invalid value for VM argument base.value");
		}
				
		final String url = System.getProperty("api.url");
		if(url == null) {
			throw new IllegalStateException("Missing VM argument api.url");
		}
		
		int anomaly_1 = 30000;
		int anomaly_2 = -90;

		while(true) {
			int start_weight = base_weight;

			// increasing the weight up by 30 till 190
			for(int i = 0; i < 30; i++) {
				post(url, start_weight++, base_weight);
			}

			// decreasing the weight up by 15 till 175
			for(int i = 0; i < 15; i++) {
				post(url, start_weight--, base_weight);
			}

			post(url, anomaly_1, base_weight);

			// decreasing the weight up by 15 till 160
			for(int i = 0; i < 15; i++) {
				post(url, start_weight--, base_weight);
			}

			post(url, anomaly_2, base_weight);
		}
	}
	
	private static void post(String url, int value, int baseWt) throws Exception {
		HTTPBuilder http = new HTTPBuilder(url);
		
		Map<String, Object> map = new HashMap<>();
		String json = "{\"timestampValue\": \"" + String.valueOf(System.currentTimeMillis()) + "\", \"actualWt\": \"" + value + "\", \"baseWt\":\"" + baseWt + "\"}";
		map.put("body", json);
		System.out.println("Posting data " + json + " to api at " + url);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/json");
		map.put("headers", headers);
		
		try {
			http.post(map);
		} catch(HttpHostConnectException | NoHttpResponseException e) {
			System.out.println("API [" + url + "] not reachable. Error - " + e.getMessage());
		}
		Thread.sleep(interval_in_ms);
	}
}
