package com.github.badabapidas.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Hello I am gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051)
              //  .usePlaintext() // this will force to deactivate the ssl during development
                .build();
        System.out.println("Creating stub");

        DummyServiceGrpc.DummyServiceBlockingStub syncClient= DummyServiceGrpc.newBlockingStub(channel);
        // DummyServiceGrpc.DummyServiceFutureStub asyncClient= DummyServiceGrpc.newFutureStub(channel);

        //do something

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

}
