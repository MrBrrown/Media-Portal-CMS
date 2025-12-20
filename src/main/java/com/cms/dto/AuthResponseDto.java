package com.cms.dto;

public class AuthResponseDto {
    private String token;
    private UserDto.UserResponseDto user;

    public AuthResponseDto() {
    }

    public AuthResponseDto(String token, UserDto.UserResponseDto user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDto.UserResponseDto getUser() {
        return user;
    }

    public void setUser(UserDto.UserResponseDto user) {
        this.user = user;
    }
}

