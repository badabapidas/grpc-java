package com.github.badabapidas.grpc.greeting.server;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void calculator(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {

        // get the inputs from the request
        int firstNumber = request.getInputs().getFirstNumber();
        int secondNumber = request.getInputs().getSecondNumber();

        // create the response
        int sum = firstNumber + secondNumber;
        CalculatorResponse response = CalculatorResponse.newBuilder()
                .setSum(sum)
                .build();

        // send the response
        responseObserver.onNext(response);

        // complete the RPC call
        responseObserver.onCompleted();

    }

    @Override
    public void primeNumer(PrimeNumberRequest request, StreamObserver<PrimeNumberResponse> responseObserver) {

        try {
            int number = request.getInput();
            int divisor = 2;
            while (number > 1) {
                if (number % divisor == 0) {
                    number = number / divisor;
                    // set the reponse
                    PrimeNumberResponse primeNumberResponse = PrimeNumberResponse.newBuilder().setResult(divisor).build();
                    responseObserver.onNext(primeNumberResponse);

                } else {
                    divisor = divisor + 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }
}
