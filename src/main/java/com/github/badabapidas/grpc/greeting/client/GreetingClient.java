package com.github.badabapidas.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Hello I am gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // this will force to deactivate the ssl during development
                .build();
        System.out.println("Creating stub");
        // old and dummy
        // DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);
        // DummyServiceGrpc.DummyServiceFutureStub asyncClient= DummyServiceGrpc.newFutureStub(channel);

        // Created a greet service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greeClient = GreetServiceGrpc.newBlockingStub(channel);
        GreetServiceGrpc.GreetServiceFutureStub greeAsyncClient = GreetServiceGrpc.newFutureStub(channel);

        //Unary calls
        /*
        // Created protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bapi")
                .setLastName("Das")
                .build();

        // do the same for GreetRequest
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();
        // Call RPC and get back the GreetResponse (protocol buffers)
        GreetResponse response = greeClient.greet(greetRequest);
        System.out.println(response.getResult());
*/
        // Server Streaming

        // prepare the request
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Bapi")).build();

        // stream the responses (in a blocking manner)
        greeClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

}
