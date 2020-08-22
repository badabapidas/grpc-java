package com.github.badabapidas.grpc.greeting.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
//import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
//import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
//import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

public class GreetingServer {

	public static final int PORT = 50051;

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("gRPC server is running...");

		// unsecure server
		 Server server = getUnSecureServer();

		// secure server
//		Server server = getSecureServerNetty();

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

	private static Server getSecureServerNetty() throws SSLException {
		InetSocketAddress address = new InetSocketAddress("localhost", PORT);
		System.out.println("Server is secure");
		SslContext sslContext = loadTLSCredentialsForServerTLS();
		return NettyServerBuilder.forAddress(address).sslContext(sslContext).addService(new GreetServiceImpl()).build();

	}

	public static SslContext loadTLSCredentialsForServerTLS() throws SSLException {
		File serverCertFile = new File(GrpcConstant.CERTIFICATE_PATH_1 + "/server.crt");
		File serverKeyFile = new File(GrpcConstant.CERTIFICATE_PATH_1 + "/server.pem");

		SslContextBuilder ctxBuilder = SslContextBuilder.forServer(serverCertFile, serverKeyFile)
				.clientAuth(ClientAuth.NONE);

		return GrpcSslContexts.configure(ctxBuilder).build();
	}
}
