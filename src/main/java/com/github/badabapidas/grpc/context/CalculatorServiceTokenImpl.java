package com.github.badabapidas.grpc.context;

import com.github.badabapidas.grpc.greeting.server.GrpcConstant;
import com.proto.calculator.AverageRequest;
import com.proto.calculator.AveragreResponse;
import com.proto.calculator.CalculatorRequest;
import com.proto.calculator.CalculatorResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeNumberRequest;
import com.proto.calculator.PrimeNumberResponse;
import com.proto.calculator.SquareRootRequest;
import com.proto.calculator.SquareRootResponse;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceTokenImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
	@Override
	public void calculator(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {

		// get the inputs from the request
		int firstNumber = request.getInputs().getFirstNumber();
		int secondNumber = request.getInputs().getSecondNumber();

		final UserInfo userInfo = (UserInfo) AuthorizationInterceptor.USER_DETAILS.get();
		System.out.println(Thread.currentThread().getName() + " --- " + userInfo);
		if (userInfo.getRoles().contains(GrpcConstant.ADMIN_ROLE)) {
			// create the response
			int sum = firstNumber + secondNumber;
			CalculatorResponse response = CalculatorResponse.newBuilder().setSum(sum).build();
			// send the response
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} else {
			responseObserver.onError(new StatusRuntimeException(Status.PERMISSION_DENIED));
		}

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
					PrimeNumberResponse primeNumberResponse = PrimeNumberResponse.newBuilder().setResult(divisor)
							.build();
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

	@Override
	public StreamObserver<AverageRequest> avergae(StreamObserver<AveragreResponse> responseObserver) {
		StreamObserver<AverageRequest> requestObserver = new StreamObserver<AverageRequest>() {

			double sum = 0;
			double count = 0;

			@Override
			public void onNext(AverageRequest value) {
				count++;
				sum += value.getInput();
//                System.out.println("count: "+count);
//                System.out.println("sum: "+sum);
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}

			@Override
			public void onCompleted() {
				double result = sum / count;
				System.out.println("result: " + result);
				responseObserver.onNext(AveragreResponse.newBuilder().setResult(result).build());
				responseObserver.onCompleted();
			}
		};

		return requestObserver;
	}

	@Override
	public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
		int number = request.getNumber();
		if (number >= 0) {
			double sqrt = Math.sqrt(number);
			responseObserver.onNext(SquareRootResponse.newBuilder().setRootNum(sqrt).build());
			responseObserver.onCompleted();
		} else {
			responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("The number is not positive")
					.augmentDescription("Number sent:" + number).asRuntimeException());
		}
	}
}
