package com.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class UserUtil {
    /**
     * Récupère l'ID de l'utilisateur connecté depuis l'en-tête HTTP
     */
    public static Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new SecurityException("Impossible d'accéder au contexte de la requête");
        }

        HttpServletRequest request = attributes.getRequest();
        String userIdStr = request.getHeader("X-User-ID");

        if (userIdStr == null || userIdStr.isEmpty()) {
            throw new SecurityException("Utilisateur non authentifié");
        }

        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new SecurityException("ID utilisateur invalide");
        }
    }

    /**
     * Vérifie si l'utilisateur courant est un administrateur
     */
    public static boolean isCurrentUserAdmin() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }

        HttpServletRequest request = attributes.getRequest();
        String userRole = request.getHeader("X-User-Role");

        return "ADMIN".equalsIgnoreCase(userRole);
    }
}