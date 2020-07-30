package com.github.badabapidas.grpc.interceptor;

import com.github.badabapidas.grpc.greeting.server.GrpcConstant;
import com.proto.calculator.CalculatorRequest;
import com.proto.calculator.CalculatorResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.proto.calculator.Inputs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {

	public static void main(String[] args) {
		ManagedChannel channel = ManagedChannelBuilder.forAddress(GrpcConstant.LOCALHOST, GrpcConstant.PORT)
				.usePlaintext().intercept(new AuthTokenProvideInterceptor()).build();

		final CalculatorServiceBlockingStub blockingStub = CalculatorServiceGrpc.newBlockingStub(channel);
		Inputs inputs = Inputs.newBuilder().setFirstNumber(4).setSecondNumber(10).build();
		CalculatorRequest request = CalculatorRequest.newBuilder().setInputs(inputs).build();
		final CalculatorResponse blockResponse = blockingStub.calculator(request);
		System.out.println("blocking call result: " + blockResponse.getSum());
		channel.shutdown();

	}
}