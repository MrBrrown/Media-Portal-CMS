package com.cms.service;

import com.cms.dto.AuthResponseDto;
import com.cms.dto.UserDto;
import com.cms.exception.BadRequestException;
import com.cms.model.entity.Role;
import com.cms.model.entity.User;
import com.cms.repository.UserRepository;
import com.cms.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Transactional
    public AuthResponseDto register(UserDto.UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(Role.USER);

        user = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());
        jwtTokenService.saveToken(token, user.getId());

        UserDto.UserResponseDto userResponse = new UserDto.UserResponseDto();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole().name());

        return new AuthResponseDto(token, userResponse);
    }

    public AuthResponseDto login(UserDto.UserLoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new com.cms.exception.ResourceNotFoundException("User not found"));

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());
        jwtTokenService.saveToken(token, user.getId());

        UserDto.UserResponseDto userResponse = new UserDto.UserResponseDto();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole().name());

        return new AuthResponseDto(token, userResponse);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new com.cms.exception.ResourceNotFoundException("User not found with id: " + id));
    }
}

