package com.github.badabapidas.grpc.greeting.server;

import javax.net.ssl.SSLException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.protobuf.services.ProtoReflectionService;

public class ServerFactory {

	public static Server getServer(GrpcConstant.TYPE type) throws SSLException {
		SslContext sslContext = null;
		switch (type) {
		case UNSECURE:
			System.out.println("Initiated for unsecure Server connection");
			return ServerBuilder.forPort(GrpcConstant.PORT).addService(new GreetServiceImpl())
					.addService(new CalculatorServiceImpl()).addService(ProtoReflectionService.newInstance()).build();

		case SERVER_TLS:
			System.out.println("Initiated for Server-Side TLS connection");
			sslContext = GreetingSSLServer.loadTLSCredentialsForServerTLS();
			return NettyServerBuilder.forPort(GrpcConstant.PORT).sslContext(sslContext)
					.addService(new GreetServiceImpl()).build();
		case MUTUAL_TLS:
			System.out.println("Initiated for Mutual TLS connection");
			sslContext = GreetingSSLServer.loadTLSCredentialsForMutualTLS();
			return NettyServerBuilder.forPort(GrpcConstant.PORT).sslContext(sslContext)
					.addService(new GreetServiceImpl()).build();

		default:
			break;
		}
		return null;
	}
}
