package com.github.badabapidas.grpc.greeting.client;

import java.io.File;

import javax.net.ssl.SSLException;

import com.github.badabapidas.grpc.greeting.server.GrpcConstant;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc.GreetServiceBlockingStub;
import com.proto.greet.Greeting;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;

public class GreetingSSLClient {

	public static void main(String[] args) throws SSLException {

		// Created protocol buffer greeting message
		System.out.println("[gRPC client]: Greeting");
		GreetingSSLClient main = new GreetingSSLClient();
		main.run();
	}

	private void run() throws SSLException {

		ManagedChannel channel = ClientFactory.getChannel(GrpcConstant.TYPE.MUTUAL_TLS);
		doUnaryCall(channel);

		System.out.println("Shutting down channel");
		if (channel != null) {
			channel.shutdown();
		}
	}

	public static SslContext loadServerSideTLSConfigs() throws SSLException {
		File serverCACertFile = new File(GrpcConstant.CERTIFICATE_PATH_2 + "/ca-cert.pem");
		return GrpcSslContexts.forClient().trustManager(serverCACertFile).build();
	}

	public static SslContext loadMutualSideTLSConfigs() throws SSLException {
		File serverCACertFile = new File(GrpcConstant.CERTIFICATE_PATH_2 + "/ca-cert.pem");
		File clientCertFile = new File(GrpcConstant.CERTIFICATE_PATH_2 + "/client-cert.pem");
		File clientKeyFile = new File(GrpcConstant.CERTIFICATE_PATH_2 + "/client-key.pem");

		return GrpcSslContexts.forClient().keyManager(clientCertFile, clientKeyFile).trustManager(serverCACertFile)
				.build();
	}

	private void doUnaryCall(ManagedChannel channel) {
		GreetServiceBlockingStub client = ClientFactory.getSyncClient(channel);

		// created a protocol buffer greeting message
		Greeting greeting = Greeting.newBuilder().setFirstName("Bapi").setLastName("Das").build();

		// do the same for a GreetRequest
		GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

		// call the RPC and get back a GreetResponse (protocol buffers)
		GreetResponse greetResponse = client.greet(greetRequest);
		System.out.println(greetResponse.getResult());

	}

}
