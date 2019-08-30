package com.github.badabapidas.grpc.greeting.client;

import com.proto.calculator.*;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    public static void main(String[] args) {
        System.out.println("Hello I am gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // this will force to deactivate the ssl during development
                .build();
        System.out.println("Creating stub");

        // Created a calculator service client (blocking - synchronous)
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = CalculatorServiceGrpc.newBlockingStub(channel);

        // Created protocol buffer calculator message
       /* Inputs inputs = Inputs.newBuilder()
                                .setFirstNumber(4)
                                .setSecondNumber(10)
                                .build();*/

        // do the same for GreetRequest
        // CalculatorRequest request = CalculatorRequest.newBuilder().setInputs(inputs).build();
        // Call RPC and get back the CalculatorResponse (protocol buffers)
        // CalculatorResponse response = calculatorClient.calculator(request);
        // System.out.println(response.getSum());

/*
        // @@@@@@@@@@ Server Streaming call
        PrimeNumberRequest request = PrimeNumberRequest.newBuilder().setInput(78987897).build();
        calculatorClient.primeNumer(request).forEachRemaining(primeNumberResponse -> {
            System.out.print(primeNumberResponse.getResult()+" ");
        });
*/

        /*// @@@@@@@@@@ Client Streaming call
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch= new CountDownLatch(1);
        StreamObserver<AverageRequest> requestObserver = asyncClient.avergae(new StreamObserver<AveragreResponse>() {
            @Override
            public void onNext(AveragreResponse value) {
                System.out.println("Receive a response from a server");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending data.");
                latch.countDown();
            }
        });

        sendData(requestObserver,1);
        sendData(requestObserver,2);
        sendData(requestObserver,3);
        sendData(requestObserver,4);

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();
        try {
            latch.await(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        // @@@@@@@@@@@@ Error handling
        int number = 10;
        try{
            SquareRootResponse squareRootResponse = calculatorClient.squareRoot(SquareRootRequest.newBuilder().setNumber(number).build());
            System.out.println("Response: "+squareRootResponse.getRootNum());
        }catch (StatusRuntimeException e){
            System.out.println("Error found: "+e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private static void sendData(StreamObserver<AverageRequest> requestObserver, double number) {
        requestObserver.onNext(AverageRequest.newBuilder().setInput(number)
                .build());
    }

}
