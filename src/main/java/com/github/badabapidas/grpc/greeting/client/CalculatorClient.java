package com.github.badabapidas.grpc.greeting.client;

import com.proto.calculator.*;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

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

        PrimeNumberRequest request = PrimeNumberRequest.newBuilder().setInput(78987897).build();
        calculatorClient.primeNumer(request).forEachRemaining(primeNumberResponse -> {
            System.out.print(primeNumberResponse.getResult()+" ");
        });

        System.out.println();
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

}
