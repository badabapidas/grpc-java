package com.github.badabapidas.grpc.greeting.server;

import com.proto.calculator.CalculatorRequest;
import com.proto.calculator.CalculatorResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void calculator(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {

        // get the inputs from the request
        int firstNumber = request.getInputs().getFirstNumber();
        int secondNumber = request.getInputs().getSecondNumber();

        // create the response
        int sum = firstNumber + secondNumber;
        CalculatorResponse response= CalculatorResponse.newBuilder()
                                                        .setSum(sum)
                                                        .build();

        // send the response
        responseObserver.onNext(response);

        // complete the RPC call
        responseObserver.onCompleted();

    }
}
