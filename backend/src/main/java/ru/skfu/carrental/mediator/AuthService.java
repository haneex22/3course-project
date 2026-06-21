package ru.skfu.carrental.mediator;

import ru.skfu.carrental.dto.request.LoginRequest;
import ru.skfu.carrental.dto.request.RegisterRequest;
import ru.skfu.carrental.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    void register(RegisterRequest request);
}
