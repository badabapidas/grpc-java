package com.github.badabapidas.grpc.context;

import com.github.badabapidas.grpc.greeting.server.GrpcConstant;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class AuthorizationInterceptor implements ServerInterceptor {

	public static final Context.Key<Object> USER_DETAILS = Context.key(GrpcConstant.USER_DETAILS);

	private UserService userService;

	public AuthorizationInterceptor() {
		this.userService = new UserServiceImpl();
	}

	public <ReqT, RespT> Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> serverCall, final Metadata metadata,
			final ServerCallHandler<ReqT, RespT> serverCallHandler) {

		final String auth_token = metadata.get(Key.of(GrpcConstant.AUTH_TOKEN, Metadata.ASCII_STRING_MARSHALLER));
		final UserInfo userInfo = userService.validate(auth_token);
		Context context = Context.current().withValue(USER_DETAILS, userInfo);
		return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);

	}
}