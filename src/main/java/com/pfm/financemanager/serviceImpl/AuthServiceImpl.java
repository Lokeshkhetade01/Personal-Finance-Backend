package com.pfm.financemanager.serviceImpl;

import com.pfm.financemanager.dto.request.LoginRequest;
import com.pfm.financemanager.dto.request.RegisterRequest;
import com.pfm.financemanager.dto.response.AuthResponse;
import com.pfm.financemanager.dto.response.UserResponse;
import com.pfm.financemanager.entity.Role;
import com.pfm.financemanager.entity.User;
import com.pfm.financemanager.exception.DuplicateResourceException;
import com.pfm.financemanager.exception.InvalidCredentialsException;
import com.pfm.financemanager.repository.UserRepository;
import com.pfm.financemanager.security.JwtService;
import com.pfm.financemanager.security.UserPrincipal;
import com.pfm.financemanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered: " + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .currency(request.getCurrency() == null ? "USD" : request.getCurrency())
                .roles(Set.of(Role.ROLE_USER))
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        UserPrincipal principal = new UserPrincipal(savedUser);
        String token = jwtService.generateToken(principal);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(mapToUserResponse(savedUser))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(mapToUserResponse(user))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .currency(user.getCurrency())
                .build();
    }
}
