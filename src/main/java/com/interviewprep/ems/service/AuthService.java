package com.interviewprep.ems.service;

import com.interviewprep.ems.dto.request.LoginRequest;
import com.interviewprep.ems.dto.request.RegisterRequest;
import com.interviewprep.ems.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
