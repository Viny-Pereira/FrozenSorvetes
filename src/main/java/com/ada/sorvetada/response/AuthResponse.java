package com.ada.sorvetada.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String acessToken;
    private String tokenType = "Bearer ";

    public AuthResponse(String acessToken) {
        this.acessToken = acessToken;
    }
}
