package com.github.badabapidas.grpc.context;

import java.util.List;

public class UserInfo {

	private String userName;
	private List<String> roles;

	public UserInfo(final String userName, final List<String> roles) {
		this.userName = userName;
		this.roles = roles;
	}

	public String getUserName() {
		return userName;
	}

	public List<String> getRoles() {
		return roles;
	}

	@Override
	public String toString() {
		return "UserInfo{" + "userName='" + userName + '\'' + ", roles=" + roles + '}';
	}
}
