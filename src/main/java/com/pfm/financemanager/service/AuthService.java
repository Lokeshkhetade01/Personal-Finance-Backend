package com.pfm.financemanager.service;

import com.pfm.financemanager.dto.request.LoginRequest;
import com.pfm.financemanager.dto.request.RegisterRequest;
import com.pfm.financemanager.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
