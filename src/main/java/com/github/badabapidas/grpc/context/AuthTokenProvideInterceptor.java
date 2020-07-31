package com.github.badabapidas.grpc.context;

import com.github.badabapidas.grpc.greeting.server.GrpcConstant;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;

public class AuthTokenProvideInterceptor implements ClientInterceptor {

	private final String authToken;

	public AuthTokenProvideInterceptor(final String authToken) {
		this.authToken = authToken;
	}

	public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> methodDescriptor,
			final CallOptions callOptions, final Channel channel) {
		return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
				channel.newCall(methodDescriptor, callOptions)) {
			@Override
			public void start(final Listener<RespT> responseListener, final Metadata headers) {
				headers.put(Key.of(GrpcConstant.AUTH_TOKEN, Metadata.ASCII_STRING_MARSHALLER), authToken);
				super.start(responseListener, headers);
			}
		};
	}
}