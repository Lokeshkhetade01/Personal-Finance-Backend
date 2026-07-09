package com.pfm.financemanager.service;

import com.pfm.financemanager.dto.request.ChangePasswordRequest;
import com.pfm.financemanager.dto.request.UpdateProfileRequest;
import com.pfm.financemanager.dto.response.UserResponse;

public interface UserService {

    UserResponse getProfile(Long userId);

    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);
}
