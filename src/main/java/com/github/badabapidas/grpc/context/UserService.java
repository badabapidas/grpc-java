package com.github.badabapidas.grpc.context;
public interface UserService {

    UserInfo validate(String authToken);
}