package com.github.badabapidas.grpc.greeting.client;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import com.github.badabapidas.grpc.greeting.server.GreetingServer;
import com.proto.greet.GreetEveryOneResponse;
import com.proto.greet.GreetEveryoneRequest;
import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetWithDeadlineRequest;
import com.proto.greet.GreetWithDeadlineResponse;
import com.proto.greet.Greeting;
import com.proto.greet.LongGreetRequest;
import com.proto.greet.LongGreetResponse;

import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GreetingClient {
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
		// without any authentication (for development)
		channel = ManagedChannelBuilder.forAddress(HOST, GreetingServer.PORT).usePlaintext().build();

		// secure channel communications
		secureChannel = NettyChannelBuilder.forAddress(HOST, 50051)
				.sslContext(GrpcSslContexts.forClient().trustManager(new File("ssl/ca.crt")).build()).build();

		// Created a greet service client (blocking - synchronous)
//		System.out.println("creating client stub");

		greeClient = GreetServiceGrpc.newBlockingStub(channel);
		// greeClient = GreetServiceGrpc.newBlockingStub(secureChannel);

		// create a async client (stub)
		// asyncClient = GreetServiceGrpc.newStub(channel);

		// doUnaryCall();
		// doServerStreamingCall();
		// doClientStreamingCall();
		// doBiDiStreamingCall();
		// doUnaryCallWithDeadline();
		 doUnaryCall(secureChannel);
		System.out.println("Shutting down channel");
		if (channel != null) {
			channel.shutdown();
		}
	}

	private void doUnaryCall(ManagedChannel channel) {
		System.out.println("Unary communication through secure channel");
		// created a greet service client (blocking - synchronous)
		GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

		// Unary
		// created a protocol buffer greeting message
		Greeting greeting = Greeting.newBuilder().setFirstName("Bapi").setLastName("Das").build();

		// do the same for a GreetRequest
		GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

		// call the RPC and get back a GreetResponse (protocol buffers)
		GreetResponse greetResponse = greetClient.greet(greetRequest);

		System.out.println(greetResponse.getResult());

	}

	private void doUnaryCallWithDeadline() {
		System.out.println("Unary communication with deadline through unsecure channel");
		// first call (3000 ms)
		try {
			GreetWithDeadlineResponse response = greeClient.withDeadline(Deadline.after(3000, TimeUnit.MILLISECONDS))
					.greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
							.setGreeting(Greeting.newBuilder().setFirstName("Bapi").build()).build());
			System.out.println("Response: " + response.getResult());
		} catch (StatusRuntimeException e) {
			if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
				System.out.println("Deadline exceeded, we dont get response");
			} else
				e.printStackTrace();
		}

		// first call (100 ms)
		try {
			GreetWithDeadlineResponse response = greeClient.withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS))
					.greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
							.setGreeting(Greeting.newBuilder().setFirstName("Bapi").build()).build());
			System.out.println("Response: " + response.getResult());
		} catch (StatusRuntimeException e) {
			System.out.println("Status: " + e.getStatus());
			if (e.getStatus().getCode() == Status.DEADLINE_EXCEEDED.getCode()) {
				System.out.println("Deadline exceeded, we dont get response");
			} else
				e.printStackTrace();
		}
	}

	private void doBiDiStreamingCall() {
		System.out.println("Bidi streaming communication through unsecure channel");
		CountDownLatch latch = new CountDownLatch(1);
		StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient
				.greetEveryone(new StreamObserver<GreetEveryOneResponse>() {
					@Override
					public void onNext(GreetEveryOneResponse value) {
						System.out.println("Response from server:" + value.getResult());
					}

					@Override
					public void onError(Throwable t) {
						latch.countDown();
					}

					@Override
					public void onCompleted() {
						System.out.println("Response from server:");
						latch.countDown();
					}
				});

		Arrays.asList("Bapi", "John", "Kalepes", "Mark").forEach(name -> {
			System.out.println("Sending: " + name);
			requestObserver.onNext(
					GreetEveryoneRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName(name)).build());

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		requestObserver.onCompleted();
		try {
			latch.await(5L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void doClientStreamingCall() {
		System.out.println("Client Streaming communication through unsecure channel");
		CountDownLatch latch = new CountDownLatch(1);
		StreamObserver<LongGreetRequest> requestObserver = asyncClient
				.longGreet(new StreamObserver<LongGreetResponse>() {
					@Override
					public void onNext(LongGreetResponse value) {
						// we get a response from a server
						System.out.println("Receive a response from a server");
						System.out.println(value.getResult());
					}

					@Override
					public void onError(Throwable t) {
						// we get an error from a server
					}

					@Override
					public void onCompleted() {
						// the server is done sending us data
						System.out.println("Server has completed sending data.");
						latch.countDown();
					}
				});

		sendDatas(requestObserver, "Bapi"); // streaming msg 1
		sendDatas(requestObserver, "Apu"); // streaming msg 2
		sendDatas(requestObserver, "Sandu"); // streaming msg 3
		sendDatas(requestObserver, "Dilip"); // streaming msg 4
		sendDatas(requestObserver, "Amit"); // streaming msg 5

		// we tell the server that the client is done sending data
		requestObserver.onCompleted();
		try {
			latch.await(5L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void sendDatas(StreamObserver<LongGreetRequest> requestObserver, String name) {
		System.out.println("Sending name: " + name);
		requestObserver.onNext(
				LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName(name).build()).build());
	}

	private void doServerStreamingCall() {
		System.out.println("Server Streaming communication through unsecure channel");
		// prepare the request
		GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
				.setGreeting(Greeting.newBuilder().setFirstName("Bapi")).build();

		// stream the responses (in a blocking manner)
		greeClient.greetManyTimes(greetManyTimesRequest).forEachRemaining(greetManyTimesResponse -> {
			System.out.println(greetManyTimesResponse.getResult());
		});

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
