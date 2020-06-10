package com.tapiax.metrics.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tapiax")
public class MetricsApplicationConfiguration {
	
	private String schemaPath;

	public String getSchemaPath() {
		return schemaPath;
	}

	public void setSchemaPath(String schemaPath) {
		this.schemaPath = schemaPath;
	}
	
	
	@Bean
	public Schema purchaseSchema() throws IOException {
		File file = new File(schemaPath);
		FileInputStream inputStream = new FileInputStream(file);
		byte[] bytes = new byte[(int) file.length()];
		inputStream.read(bytes);
		inputStream.close();
		String jsonString = new String(bytes,"UTF-8");
		JSONObject jsonObject = new JSONObject(jsonString);
		return SchemaLoader.load(jsonObject);
	} 
	
	
}
