package com.cms.service;

import com.cms.dto.AuthResponseDto;
import com.cms.dto.UserDto;
import com.cms.exception.BadRequestException;
import com.cms.model.entity.Role;
import com.cms.model.entity.User;
import com.cms.repository.UserRepository;
import com.cms.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);
    }

    @Test
    void testRegisterSuccess() {
        UserDto.UserRegistrationDto registrationDto = new UserDto.UserRegistrationDto();
        registrationDto.setUsername("newuser");
        registrationDto.setEmail("new@example.com");
        registrationDto.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(anyString(), any())).thenReturn("test-token");

        AuthResponseDto response = userService.register(registrationDto);

        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        assertNotNull(response.getUser());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtTokenService, times(1)).saveToken(anyString(), any());
    }

    @Test
    void testRegisterUsernameExists() {
        UserDto.UserRegistrationDto registrationDto = new UserDto.UserRegistrationDto();
        registrationDto.setUsername("existinguser");
        registrationDto.setEmail("new@example.com");
        registrationDto.setPassword("password123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.register(registrationDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterEmailExists() {
        UserDto.UserRegistrationDto registrationDto = new UserDto.UserRegistrationDto();
        registrationDto.setUsername("newuser");
        registrationDto.setEmail("existing@example.com");
        registrationDto.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.register(registrationDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        UserDto.UserLoginDto loginDto = new UserDto.UserLoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(anyString(), any())).thenReturn("test-token");

        AuthResponseDto response = userService.login(loginDto);

        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        assertNotNull(response.getUser());
        verify(jwtTokenService, times(1)).saveToken(anyString(), any());
    }
}

