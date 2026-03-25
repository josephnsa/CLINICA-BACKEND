package com.clinica.salud.shared.response;

import java.time.LocalDateTime;

/**
 * Envoltura estándar para respuestas de la API.
 * Record genérico con timestamp por defecto en now().
 *
 * @param <T> tipo del payload de la respuesta
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        LocalDateTime timestamp
) {
    /**
     * Compact constructor: si timestamp es null, se usa LocalDateTime.now().
     */
    public ApiResponse {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    /**
     * Respuesta exitosa solo con data (message null, timestamp now).
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, null, data, null);
    }

    /**
     * Respuesta exitosa con mensaje y data (timestamp now).
     */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    /**
     * Respuesta de error con mensaje (data null, timestamp now).
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }
}
