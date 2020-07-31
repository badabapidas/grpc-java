package com.github.badabapidas.grpc.context;

import java.util.ArrayList;
import java.util.List;

import com.github.badabapidas.grpc.greeting.server.GrpcConstant;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class UserServiceImpl implements UserService {
	@Override
	public UserInfo validate(final String authToken) {

		if (authToken == null) {
			throw new StatusRuntimeException(Status.UNAUTHENTICATED);
		}

		return loadUserByAuthToken(authToken);
	}

	private UserInfo loadUserByAuthToken(final String authToken) {

		if (authToken.equals(GrpcConstant.ADMIN_TOKEN)) {
			List<String> roles = new ArrayList<>();
			roles.add(GrpcConstant.ADMIN_ROLE);
			roles.add(GrpcConstant.USER_ROLE);
			return new UserInfo("Bapi", roles);
		}

		List<String> roles = new ArrayList<>();
		roles.add(GrpcConstant.USER_ROLE);
		return new UserInfo("Sreejesh", roles);
	}
}