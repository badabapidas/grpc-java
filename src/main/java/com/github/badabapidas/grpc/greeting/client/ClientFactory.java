package com.github.badabapidas.grpc.greeting.client;

import javax.net.ssl.SSLException;

import com.github.badabapidas.grpc.greeting.server.GreetingServer;
import com.github.badabapidas.grpc.greeting.server.GrpcConstant;
import com.proto.greet.GreetServiceGrpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;

public class ClientFactory {

	public static ManagedChannel getChannel(GrpcConstant.TYPE type) throws SSLException {
		SslContext sslContext = null;
		switch (type) {
		case UNSECURE:
			System.out.println("Initiated for unsecure channel connection");
			return ManagedChannelBuilder.forAddress(GrpcConstant.HOST, GreetingServer.PORT).usePlaintext().build();
		case SERVER_TLS:
			System.out.println("Initiated for Server-Side TLS channel connection");
			sslContext = GreetingSSLClient.loadServerSideTLSConfigs();
			return NettyChannelBuilder.forAddress(GrpcConstant.HOST, GreetingServer.PORT).sslContext(sslContext)
					.build();
		case MUTUAL_TLS:
			System.out.println("Initiated for Mutual TLS channel connection");
			sslContext = GreetingSSLClient.loadMutualSideTLSConfigs();
			return NettyChannelBuilder.forAddress(GrpcConstant.HOST, GreetingServer.PORT).sslContext(sslContext)
					.build();

		default:
			break;
		}
		return null;

	}

	public static GreetServiceGrpc.GreetServiceBlockingStub getSyncClient(Channel channel) {
		System.out.println("Synchronous Client");
		return GreetServiceGrpc.newBlockingStub(channel);
	}

	public static GreetServiceGrpc.GreetServiceStub getAsyncClient(Channel channel) {
		System.out.println("Asynchronous Client");
		return GreetServiceGrpc.newStub(channel);
	}
}
