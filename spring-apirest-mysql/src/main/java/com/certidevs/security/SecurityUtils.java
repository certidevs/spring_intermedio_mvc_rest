package com.certidevs.security;

import com.certidevs.model.Role;
import com.certidevs.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Devuelve el usuario autenticado extraído de Spring Security
     *
     * Se utiliza así:
     *
     * User user = SecurityUtils.getCurrentUser().orElseThrow();
     * @return
     */
    public static Optional<User> getCurrentUser() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User user) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }

    }

    public static boolean isAdminCurrentUser() {

        if (getCurrentUser().isEmpty()) {
            return false;
        }

        User user = getCurrentUser().get();
        return user.getRole().equals(Role.ADMIN);
    }

}