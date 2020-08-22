package com.github.badabapidas.grpc.greeting.server;

import com.github.badabapidas.grpc.jwt.Constants;
import com.proto.greet.*;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
	@Override
	public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {

		String clientId = Constants.CLIENT_ID_CONTEXT_KEY.get();
		System.out.println("Processing request from " + clientId);

		if (!clientId.startsWith("Greeting")) {
			responseObserver.onError(new StatusRuntimeException(Status.PERMISSION_DENIED));
		} else {

			// extract the fields from request
			Greeting greeting = request.getGreeting();
			String firstName = greeting.getFirstName();
			String lastName = greeting.getLastName();

			// create the response
			String result = "Hello " + firstName + " " + lastName;
			GreetResponse response = GreetResponse.newBuilder().setResult(result).build();

			// send the response
			responseObserver.onNext(response);

			// complete the RPC call
			responseObserver.onCompleted();
		}
	}

	@Override
	public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {

		try {
			// extract the fields from request
			Greeting greeting = request.getGreeting();
			String firstName = greeting.getFirstName();

			for (int i = 0; i < 10; i++) {
				String result = "Hello " + firstName + ", Response no: " + i;
				GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder().setResult(result).build();
				responseObserver.onNext(response);
				Thread.sleep(1000L);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			responseObserver.onCompleted();
		}
	}

	@Override
	public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
		StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {

			String result = "";

			@Override
			public void onNext(LongGreetRequest value) {
				// client sends a message
				result += "Hello " + value.getGreeting().getFirstName() + "! ";
			}

			@Override
			public void onError(Throwable t) {
				// client sends an error
			}

			@Override
			public void onCompleted() {
				// client is done
				responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
				responseObserver.onCompleted();
			}
		};
		return requestObserver;
	}

	@Override
	public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryOneResponse> responseObserver) {
		StreamObserver<GreetEveryoneRequest> requestStreamObserver = new StreamObserver<GreetEveryoneRequest>() {
			@Override
			public void onNext(GreetEveryoneRequest value) {
				String response = "Hello  " + value.getGreeting().getFirstName();
				GreetEveryOneResponse greetEveryOneResponse = GreetEveryOneResponse.newBuilder().setResult(response)
						.build();
				responseObserver.onNext(greetEveryOneResponse);
			}

			@Override
			public void onError(Throwable t) {

			}

			@Override
			public void onCompleted() {
				responseObserver.onCompleted();
			}
		};

		return requestStreamObserver;
	}

	@Override
	public void greetWithDeadline(GreetWithDeadlineRequest request,
			StreamObserver<GreetWithDeadlineResponse> responseObserver) {
		Context context = Context.current();
		try {
			for (int i = 0; i < 3; i++) {
				System.out.println("Sleeping");
				if (!context.isCancelled()) {
					Thread.sleep(100);
				} else
					return;
			}

			System.out.println("send response");
			responseObserver.onNext(GreetWithDeadlineResponse.newBuilder()
					.setResult("Hello, " + request.getGreeting().getFirstName()).build());
			responseObserver.onCompleted();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
