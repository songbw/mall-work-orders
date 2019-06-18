package com.fengchao.workorders.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


public class PasswordEncodeUtil {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String passwordEncoder(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean isMatch(String fromFront, String fromDb) {
        return passwordEncoder.matches(fromFront,fromDb);
    }
}
