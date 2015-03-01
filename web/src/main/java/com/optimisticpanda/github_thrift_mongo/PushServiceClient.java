package com.optimisticpanda.github_thrift_mongo;

import github.thrift.mongo.core.api.Push;
import github.thrift.mongo.core.api.PushService;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public class PushServiceClient {

	private static Logger log = LoggerFactory.getLogger(PushServiceClient.class);
	private String pushServiceHost;
	private int pushServicePort;

	public PushServiceClient(Env env) {
		this.pushServiceHost = env.pushServiceHost();
		this.pushServicePort = env.pushServicePort();
	}

	private <T> Optional<T> execute(Function<PushService.Client, T> call) {
		TTransport transport = new TFramedTransport(new TSocket(pushServiceHost, pushServicePort));
		try {
			transport.open();
			TProtocol protocol = new TCompactProtocol(transport);
			PushService.Client client = new PushService.Client(protocol);
			return Optional.of(call.apply(client));
		} catch (TException e) {
			log.error("Problem connecting to service", e);
			return Optional.empty();
		} finally {
			if (transport.isOpen()) {
				transport.close();
			}
		}
	}

	public int getTotalNumberOfPushes() {
		return execute(client -> {
			try {
				return client.getTotalNumberOfPushes();
			} catch (TException e) {
				log.error("Problem calling service", e);
				return 0;
			}
		}).orElse(Integer.valueOf(0));
	}

	public Set<Push> query(String query) {
		return execute(client -> {
			try {
				return client.getPushes(query);
			} catch (TException e) {
				throw Throwables.propagate(e);
			}
		}).orElse(Collections.emptySet());
	}

	public boolean isHealthy() {
		return execute(client -> {
			try {
				return client.ping().contains("pong");
			} catch (TException e) {
				log.error("Problem calling service", e);
				return false;
			}
		}).orElse(Boolean.FALSE);
	}

	public Status getStatus(){
		return new Status(getTotalNumberOfPushes(), isHealthy());
	}
	
	public static class Status {
		private int eventCount;
		private boolean healthy;

		public Status(int eventCount, boolean healthy) {
			this.eventCount = eventCount;
			this.healthy = healthy;
		}
		@Override
		public String toString() {
			return "Status [eventCount=" + eventCount + ", healthy=" + healthy + "]";
		}
	}
}
