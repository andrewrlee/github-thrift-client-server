package com.optimisticpanda.github_thrift_mongo;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Route;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheck.Result;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.json.HealthCheckModule;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

public class SparkMetricUtils {

	private static Logger log = LoggerFactory.getLogger(SparkMetricUtils.class);
	
	public static Route metricsResponse(MetricRegistry metrics) {
		ObjectMapper mapper = new ObjectMapper().registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, false, MetricFilter.ALL));
		return (req, resp) -> {
			try {
				resp.type("application/json");
				return mapper.writeValueAsString(metrics);
			} catch (JsonProcessingException e) {
				throw Throwables.propagate(e);
			}
		};
	}

	public static Route healthchecksResponse(final HealthCheckRegistry healthchecks) {
		ObjectMapper mapper = new ObjectMapper().registerModule(new HealthCheckModule());
		return (req, resp) -> {
			try {
				SortedMap<String, Result> results = healthchecks.runHealthChecks();
				boolean allHealthy = results.values().stream().allMatch(r -> r.isHealthy());
				resp.status(allHealthy ? 200 : 500);
				resp.type("application/json");
				return mapper.writeValueAsString(results);
			} catch (JsonProcessingException e) {
				throw Throwables.propagate(e);
			}
		};
	}

	public static Function<Route, Route> timed(MetricRegistry metrics, String name) {
		return (route) -> (req, resp) -> {
			final Timer.Context context = metrics.timer(name + "-timer").time();
			try {
				return route.handle(req, resp);
			} finally {
				context.stop();
			}
		};
	}

	public static Function<Route, Route> counted(MetricRegistry metrics, String name) {
		return (route) -> (req, resp) -> {
			metrics.counter(name + "-count").inc();
			return route.handle(req, resp);
		};
	}
	
	public static Function<Route, Route> logPathParams() {
		return (route) -> (req, resp) -> {
			log.info("Path Params: {}", req.params());
			return route.handle(req, resp);
		};
	}

	@SafeVarargs
	public static Route decorate(Route route, Function<Route, Route>... functions) {
		Function<Route, Route> identity = r -> r;
		BinaryOperator<Function<Route, Route>> compose = (a, b) -> (r) -> a.apply(b.apply(r));
		
		return Arrays.stream(functions)//
				.reduce(identity, compose).apply(route);
	}

}
