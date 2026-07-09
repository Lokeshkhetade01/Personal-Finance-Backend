package com.pfm.financemanager.serviceImpl;

import com.pfm.financemanager.dto.request.ChangePasswordRequest;
import com.pfm.financemanager.dto.request.UpdateProfileRequest;
import com.pfm.financemanager.dto.response.UserResponse;
import com.pfm.financemanager.entity.User;
import com.pfm.financemanager.exception.InvalidCredentialsException;
import com.pfm.financemanager.exception.ResourceNotFoundException;
import com.pfm.financemanager.repository.UserRepository;
import com.pfm.financemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getProfile(Long userId) {
        User user = findUser(userId);
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUser(userId);

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName());
        }
        if (StringUtils.hasText(request.getPhoneNumber())) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (StringUtils.hasText(request.getCurrency())) {
            user.setCurrency(request.getCurrency());
        }

        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = findUser(userId);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .currency(user.getCurrency())
                .build();
    }
}
