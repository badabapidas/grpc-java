package com.github.badabapidas.grpc.jwt;

import java.io.IOException;

import com.github.badabapidas.grpc.greeting.server.GreetServiceImpl;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GreetingServer {
	private static final int PORT = 50051;

	public static void main(String[] args) throws IOException, InterruptedException {
		Server server = ServerBuilder.forPort(PORT).addService(new GreetServiceImpl())
				.intercept(new AuthorizationServerInterceptor()).build();

		server.start();
		System.out.println("gRPC Server started, listening on port:" + server.getPort());
		System.out.println("JWT based token enabled");
		server.awaitTermination();
	}
}