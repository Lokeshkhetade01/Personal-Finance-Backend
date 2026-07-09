package com.pfm.financemanager.controller;

import com.pfm.financemanager.dto.request.ChangePasswordRequest;
import com.pfm.financemanager.dto.request.UpdateProfileRequest;
import com.pfm.financemanager.dto.response.ApiResponse;
import com.pfm.financemanager.dto.response.UserResponse;
import com.pfm.financemanager.security.SecurityUtil;
import com.pfm.financemanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        UserResponse response = userService.getProfile(SecurityUtil.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", response));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = userService.updateProfile(SecurityUtil.getCurrentUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(SecurityUtil.getCurrentUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }
}
