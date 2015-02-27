package com.optimisticpanda.github_thrift_mongo;

import static spark.Spark.*;

public class Main {

	private static int port = Integer.valueOf(
			System.getenv().getOrDefault("APP_SERVER_PORT", "8080")).intValue();

	public static void main(String[] args) {

		port(port);

		get("/hello", (req, res) -> "Hello World");
	}
}
