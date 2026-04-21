package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto;

/**
 * Solución Regla 2 y 15: DTO de salida modelado como record (inmutable).
 */
public record UserResponse(
    String id,
    String name,
    String email,
    String role,
    String status
) {}
