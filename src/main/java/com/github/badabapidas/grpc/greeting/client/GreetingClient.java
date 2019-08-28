package com.github.badabapidas.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    private ManagedChannel channel;
    private GreetServiceGrpc.GreetServiceBlockingStub greeClient;
    private GreetServiceGrpc.GreetServiceStub asyncClient;

    private void run() {
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // this will force to deactivate the ssl during development
                .build();

        // Created a greet service client (blocking - synchronous)
        System.out.println("Creating stub");
        greeClient = GreetServiceGrpc.newBlockingStub(channel);
        // create a async client (stub)
        asyncClient = GreetServiceGrpc.newStub(channel);

//        doUnaryCall();
//        doServerStreamingCall();
        doClientStreamingCall();

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void doClientStreamingCall() {
        CountDownLatch latch= new CountDownLatch(1);
        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
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

        sendDatas(requestObserver,"Bapi"); // streaming msg 1
        sendDatas(requestObserver,"Apu"); // streaming msg 2
        sendDatas(requestObserver,"Sandu"); // streaming msg 3
        sendDatas(requestObserver,"Dilip"); // streaming msg 4
        sendDatas(requestObserver,"Amit"); // streaming msg 5

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();
        try {
            latch.await(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void sendDatas(StreamObserver<LongGreetRequest> requestObserver, String name) {
        System.out.println("Sending name: "+name);
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName(name).build())
                .build());
    }



    private void doServerStreamingCall() {
        // prepare the request
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Bapi")).build();

        // stream the responses (in a blocking manner)
        greeClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });

    }

    private void doUnaryCall() {

        // Created protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bapi")
                .setLastName("Das")
                .build();

        // Prepare REQUEST
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        // Call RPC and get back the GreetResponse (protocol buffers)
        GreetResponse response = greeClient.greet(greetRequest);
        System.out.println(response.getResult());
    }

    public static void main(String[] args) {

        // Created protocol buffer greeting message
        System.out.println("Hello I am gRPC client");
        GreetingClient main = new GreetingClient();
        main.run();
    }

}
