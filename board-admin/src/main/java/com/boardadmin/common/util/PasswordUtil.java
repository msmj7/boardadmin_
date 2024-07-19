package com.boardadmin.common.util;

import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil { // 중복인코딩방지 분리 

    private PasswordUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String encodePassword(PasswordEncoder passwordEncoder, String Password) {
        if (Password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (!Password.startsWith("$2a$")) {
            return passwordEncoder.encode(Password);
        }
        return Password;
    }
}
