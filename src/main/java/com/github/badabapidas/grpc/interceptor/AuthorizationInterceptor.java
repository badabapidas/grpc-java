/**
 * Its a simple validation, which expects auth_token to be present and it should be equal to ‘valid_token’
 */
package com.github.badabapidas.grpc.interceptor;

import com.github.badabapidas.grpc.greeting.server.GrpcConstant;

import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class AuthorizationInterceptor implements ServerInterceptor {

	@Override
	public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata,
			ServerCallHandler<ReqT, RespT> serverCallHandler) {
		final String auth_token = metadata.get(Key.of(GrpcConstant.AUTH_TOKEN, Metadata.ASCII_STRING_MARSHALLER));
		if (auth_token == null || !auth_token.equals(GrpcConstant.VALID_TOKEN)) {
			throw new StatusRuntimeException(Status.FAILED_PRECONDITION);
		}
		return serverCallHandler.startCall(serverCall, metadata);
	}

}