package com.github.badabapidas.grpc.jwt;

import java.util.concurrent.Executor;

import io.grpc.Attributes;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

public class BearerToken implements CallCredentials {

	private String value;

	BearerToken(String value) {
		this.value = value;
	}

	@Override
	public void thisUsesUnstableApi() {
	}

	@Override
	public void applyRequestMetadata(MethodDescriptor<?, ?> method, Attributes attrs, Executor executor,
			MetadataApplier metadataApplier) {
		executor.execute(() -> {
			try {
				Metadata headers = new Metadata();
				headers.put(Constants.AUTHORIZATION_METADATA_KEY, String.format("%s %s", Constants.BEARER_TYPE, value));
				metadataApplier.apply(headers);
			} catch (Throwable e) {
				metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
			}
		});

	}
}
