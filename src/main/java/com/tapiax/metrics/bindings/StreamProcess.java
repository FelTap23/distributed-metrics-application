package com.tapiax.metrics.bindings;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface StreamProcess {

	public String input = "input";
	public String output = "output";

	@Input(input)
	public KStream<String, String> inputProcess();

	@Output(output)
	public KStream<String, String> outputProcess();
	
}
