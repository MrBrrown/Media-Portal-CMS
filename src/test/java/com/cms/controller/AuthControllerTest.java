package com.cms.controller;

import com.cms.dto.AuthResponseDto;
import com.cms.dto.UserDto;
import com.cms.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister() throws Exception {
        UserDto.UserRegistrationDto registrationDto = new UserDto.UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password123");

        UserDto.UserResponseDto userResponse = new UserDto.UserResponseDto();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setRole("USER");

        AuthResponseDto authResponse = new AuthResponseDto("test-token", userResponse);

        when(userService.register(any(UserDto.UserRegistrationDto.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void testLogin() throws Exception {
        UserDto.UserLoginDto loginDto = new UserDto.UserLoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        UserDto.UserResponseDto userResponse = new UserDto.UserResponseDto();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setRole("USER");

        AuthResponseDto authResponse = new AuthResponseDto("test-token", userResponse);

        when(userService.login(any(UserDto.UserLoginDto.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }
}

