package com.github.badabapidas.grpc.greeting.server;

import java.io.File;
import java.io.IOException;

import javax.net.ssl.SSLException;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;

public class GreetingSSLServer {

	public static void main(String[] args) throws IOException, InterruptedException {
		Server server = ServerFactory.getServer(GrpcConstant.TYPE.MUTUAL_TLS);
		System.out.println("gRPC server is running...");
		server.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Received shutdown request");
			server.shutdown();
			System.out.println("Successfully stopped the server");
		}));
		server.awaitTermination();
	}

	/**
	 * Method to load Server side tls credential files
	 * 
	 * @return
	 * @throws SSLException
	 */
	public static SslContext loadTLSCredentialsForServerTLS() throws SSLException {
		File serverCertFile = new File(GrpcConstant.CERTIFICATE_PATH_2 + "/server-cert.pem");
		File serverKeyFile = new File(GrpcConstant.CERTIFICATE_PATH_2 + "/server-key.pem");

		SslContextBuilder ctxBuilder = SslContextBuilder.forServer(serverCertFile, serverKeyFile)
				.clientAuth(ClientAuth.NONE);

		return GrpcSslContexts.configure(ctxBuilder).build();
	}

	/**
	 * Method to load mutual tls credential files
	 * 
	 * @return
	 * @throws SSLException
	 */
	public static SslContext loadTLSCredentialsForMutualTLS() throws SSLException {
		File serverCertFile = new File(GrpcConstant.CERTIFICATE_PATH_2 + "/server-cert.pem");
		File serverKeyFile = new File(GrpcConstant.CERTIFICATE_PATH_2 + "/server-key.pem");
		File clientCACertFile = new File(GrpcConstant.CERTIFICATE_PATH_2 + "/ca-cert.pem");

		SslContextBuilder ctxBuilder = SslContextBuilder.forServer(serverCertFile, serverKeyFile)
				.clientAuth(ClientAuth.REQUIRE).trustManager(clientCACertFile);

		return GrpcSslContexts.configure(ctxBuilder).build();
	}

}
