package com.slim.service;

import com.slim.domain.Admin;
import com.slim.repository.AdminRepository;
import com.slim.utils.PasswordUtil;

public class AuthService {

    private final AdminRepository adminRepository;
    private Admin loggedInAdmin;

    public AuthService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public boolean login(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) return false;
        if (!PasswordUtil.matches(password, admin.getPassword())) return false;
        loggedInAdmin = admin;
        return true;
    }

    public void logout() {
        loggedInAdmin = null;
    }

    public boolean isLoggedIn() {
        return loggedInAdmin != null;
    }

    public Admin getLoggedInAdmin() {
        return loggedInAdmin;
    }
}
