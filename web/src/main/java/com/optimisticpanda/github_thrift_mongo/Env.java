package com.optimisticpanda.github_thrift_mongo;

import com.google.common.primitives.Ints;

public class Env {

	private String getEnvVariable(String variable, String defaultValue) {
		return System.getenv().getOrDefault(variable, defaultValue);
	}

	public int serverPort() {
		return Ints.tryParse(getEnvVariable("APP_SERVER_PORT", "8080"));
	}

	public int pushServicePort() {
		return Ints.tryParse(getEnvVariable("APP_PUSH_SERVICE_PORT", "8080"));
	}

	public String pushServiceHost() {
		return getEnvVariable("APP_PUSH_SERVICE_HOST", "localhost");
	}
}
