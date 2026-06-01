package com.slim.controller;

import com.slim.domain.Admin;
import com.slim.service.AuthService;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public boolean login(String username, String password) {
        return authService.login(username, password);
    }

    public void logout() {
        authService.logout();
    }

    public boolean isLoggedIn() {
        return authService.isLoggedIn();
    }

    public Admin getLoggedInAdmin() {
        return authService.getLoggedInAdmin();
    }
}
