package com.github.badabapidas.grpc.jwt;

import javax.net.ssl.SSLException;

import com.github.badabapidas.grpc.greeting.server.GreetingServer;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class GreetingClient {
	private static final String CLIENT_IDENTIFICFIER = "GreetingClient1";
	private static final String UHM = "uhm";
	private static final String HOST = "localhost";
	private ManagedChannel channel;
	private ManagedChannel secureChannel;
	private GreetServiceGrpc.GreetServiceBlockingStub greeClient;
	private GreetServiceGrpc.GreetServiceStub asyncClient;

	public static void main(String[] args) throws SSLException {

		// Created protocol buffer greeting message
		System.out.println("[gRPC client]: Greeting");
		GreetingClient main = new GreetingClient();
		main.run();
	}

	private void run() throws SSLException {

		channel = NettyChannelBuilder.forAddress(HOST, GreetingServer.PORT).usePlaintext().build();

		BearerToken token = new BearerToken(getJwt());
		greeClient = GreetServiceGrpc.newBlockingStub(channel).withCallCredentials(token);

		doUnaryCall();
		System.out.println("Shutting down channel");
		if (channel != null) {
			channel.shutdown();
		}
	}

	private static String getJwt() {
		return Jwts.builder().setSubject(CLIENT_IDENTIFICFIER) // client's identifier
				.setId(UHM).signWith(SignatureAlgorithm.HS256, Constants.JWT_SIGNING_KEY).compact();
	}

	private void doUnaryCall() {
		System.out.println("Unary communication through unsecure channel");
		// Created protocol buffer greeting message
		Greeting greeting = Greeting.newBuilder().setFirstName("Bapi").setLastName("Das").build();

		// Prepare REQUEST
		GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

		// Call RPC and get back the GreetResponse (protocol buffers)
		GreetResponse response = greeClient.greet(greetRequest);
		System.out.println(response.getResult());
	}

}
