package com.optimisticpanda.github_thrift_mongo;

import static com.optimisticpanda.github_thrift_mongo.SparkMetricUtils.chain;
import static com.optimisticpanda.github_thrift_mongo.SparkMetricUtils.healthchecksResponse;
import static com.optimisticpanda.github_thrift_mongo.SparkMetricUtils.metricsResponse;
import static com.optimisticpanda.github_thrift_mongo.SparkMetricUtils.timed;
import static spark.Spark.get;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.health.HealthCheckRegistry;

public class Main {

	public static void main(String[] args) {
		Env env = new Env();
		MetricRegistry metrics = new MetricRegistry();
		HealthCheckRegistry healthchecks = new HealthCheckRegistry();
		
		PushServiceClient service = new PushServiceClient(env, metrics);
		healthchecks.register("client", service.getHealthcheck());
		
		staticFileLocation("/public");
		port(env.serverPort());

		get("/service/status", //
				chain(timed(metrics, "api-status"), //
						(req, res) -> service.getStatus()), JsonResponseTransformer.INSTANCE);
		get("/service/search/:query", //
				chain(timed(metrics, "api-search"), //
						(req, res) -> service.query(req.params(":query"))), JsonResponseTransformer.INSTANCE);

		get("/admin/metrics", metricsResponse(metrics));
		get("/admin/health", healthchecksResponse(healthchecks));
		
		Graphite graphite = new Graphite(new InetSocketAddress(env.graphiteHost(), env.graphitePort()));
		GraphiteReporter reporter = GraphiteReporter.forRegistry(metrics)
		                                                  .prefixedWith(env.graphitePrefix())
		                                                  .convertRatesTo(TimeUnit.SECONDS)
		                                                  .convertDurationsTo(TimeUnit.MILLISECONDS)
		                                                  .filter(MetricFilter.ALL)
		                                                  .build(graphite);
		reporter.start(1, TimeUnit.MINUTES);
	}

}
