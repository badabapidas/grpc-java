package com.github.badabapidas.grpc.greeting.server;

import java.io.File;
import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

public class GreetingServer {

	public static final int PORT = 50051;

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("gRPC server is running...");

		// unsecure server
//		 Server server = getUnSecureServer();

		// secure server
		Server server = getSecureServer();

		server.start();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Received shutdown request");
			server.shutdown();
			System.out.println("Successfully stopped the server");
		}));
		server.awaitTermination();
	}

	/**
	 * Method to create a unsecure server; which can be used for development purpose
	 * 
	 * @return Server
	 */
	private static Server getUnSecureServer() {
		System.out.println("Server is not secure");
		return ServerBuilder.forPort(PORT).addService(new GreetServiceImpl()).addService(new CalculatorServiceImpl())
				.addService(ProtoReflectionService.newInstance()).build();
	}

	/**
	 * Method to create a secure server using ssl
	 * 
	 * @return Server
	 */
	private static Server getSecureServer() {
		System.out.println("Server is secure");
		return ServerBuilder.forPort(PORT).addService(new GreetServiceImpl())
				.useTransportSecurity(new File(GrpcConstant.CERTIFICATE_PATH_1 + "/server.crt"),
						new File(GrpcConstant.CERTIFICATE_PATH_1 + "/server.pem"))
				.build();

	}
}
