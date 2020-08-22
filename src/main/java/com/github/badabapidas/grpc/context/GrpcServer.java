package com.github.badabapidas.grpc.context;

import com.github.badabapidas.grpc.greeting.server.GrpcConstant;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.NettyServerBuilder;

public class GrpcServer {

	public static void main(String[] args) throws Exception {
		System.out.println("gRPC server is running...");
		final Server server = NettyServerBuilder.forPort(GrpcConstant.PORT).addService(new CalculatorServiceTokenImpl())
				.intercept(new AuthorizationInterceptor()).build();

		server.start();
		server.awaitTermination();
	}
}