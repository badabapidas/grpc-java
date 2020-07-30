package com.github.badabapidas.grpc.interceptor;

import com.github.badabapidas.grpc.greeting.server.CalculatorServiceImpl;
import com.github.badabapidas.grpc.greeting.server.GrpcConstant;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {

	public static void main(String[] args) throws Exception {
		System.out.println("gRPC server is running...");
		final Server server = ServerBuilder.forPort(GrpcConstant.PORT).addService(new CalculatorServiceImpl())
				.intercept(new AuthorizationInterceptor()).build();

		server.start();
		server.awaitTermination();
	}
}