package com.optimisticpanda.github_thrift_mongo;

import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import spark.ResponseTransformer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;

public enum JsonResponseTransformer implements ResponseTransformer {
	INSTANCE;

	private final ObjectMapper mapper = new ObjectMapper()//
			.setVisibility(ALL, Visibility.NONE)//
			.setVisibility(FIELD, Visibility.ANY);

	@Override
	public String render(Object model) throws Exception {
		return mapper.writeValueAsString(model);
	}

}
