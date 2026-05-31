package com.quizvault.service;

import com.quizvault.dto.AuthRequest;
import com.quizvault.dto.AuthResponse;
import com.quizvault.dto.UserDto;
import com.quizvault.entity.User;
import com.quizvault.repository.UserRepository;
import com.quizvault.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        String role = request.getRole() != null ? request.getRole().toLowerCase() : "student";
        String displayName = request.getDisplayName() != null ? request.getDisplayName() : request.getEmail().split("@")[0];

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(displayName)
                .role(role)
                .build();

        user = userRepository.save(user);

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole(), user.getId());
        UserDto userDto = UserDto.builder()
                ._id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .build();

        return new AuthResponse(token, userDto);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Optional role check if requested
        if (request.getRole() != null && !user.getRole().equalsIgnoreCase(request.getRole())) {
            user.setRole(request.getRole().toLowerCase());
            userRepository.save(user);
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole(), user.getId());
        UserDto userDto = UserDto.builder()
                ._id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .build();

        return new AuthResponse(token, userDto);
    }

    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserDto.builder()
                ._id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .build();
    }
}
