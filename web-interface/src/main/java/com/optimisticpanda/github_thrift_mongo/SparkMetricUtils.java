package com.optimisticpanda.github_thrift_mongo;

import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

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
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class SparkMetricUtils {

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

	public static Route timed(MetricRegistry metrics, String name) {
		return (req, resp) -> {
			final Timer.Context context = metrics.timer(name + "-timer").time();
			try {
				return null;
			} finally {
				context.stop();
			}
		};
	}

	public static Route counted(MetricRegistry metrics, String name) {
		return (req, resp) -> {
			metrics.counter(name + "-count").inc();
			return null;
		};
	}

	public static Route firstNonNullResult(Route route1, Route route2) {
		return (req, resp) -> {
			return Objects.firstNonNull(route1.handle(req, resp), route2.handle(req, resp));
		};
	}

	@SafeVarargs
	public static Route chain(Route function, Route... functions) {
		List<Route> reversed = Lists.reverse(Lists.asList(function, functions));
		return reversed.stream().reduce(SparkMetricUtils::firstNonNullResult).get();
	}

}
