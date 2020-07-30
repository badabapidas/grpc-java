package com.github.badabapidas.grpc.greeting.server;

public class GrpcConstant {

	public static final String CERTIFICATE_PATH_1 = "certs/ssl1";
	public static final String CERTIFICATE_PATH_2 = "certs/ssl2";

	public static final int PORT = 50051;
	public static final String HOST = "0.0.0.0";
	public static final String LOCALHOST = "localhost";

	public enum TYPE {
		UNSECURE, SERVER_TLS, MUTUAL_TLS
	}

	public static final String AUTH_TOKEN = "auth_token";
	public static final String VALID_TOKEN = "valid_token";
	public static final String INVALID_TOKEN = "invalid_token";
}
