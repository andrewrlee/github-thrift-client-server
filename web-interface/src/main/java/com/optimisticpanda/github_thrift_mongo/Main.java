package com.optimisticpanda.github_thrift_mongo;

import static spark.Spark.get;
import static spark.SparkBase.port;
import static spark.SparkBase.*;

public class Main {

	public static void main(String[] args) {
		Env env = new Env();
		PushServiceClient service = new PushServiceClient(env);
		
		staticFileLocation("/public");
		port(env.serverPort());
		
		get("/service/status", (req, res) -> service.getStatus(), JsonResponseTransformer.INSTANCE);
		get("/service/search/:query", (req, res) -> service.query(req.params(":query")), JsonResponseTransformer.INSTANCE);
	}
}
