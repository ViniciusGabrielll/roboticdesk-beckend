package com.vinicius.roboticdesk.controller.dto;

import com.vinicius.roboticdesk.entities.Role;

import java.util.Set;
import java.util.UUID;

public record MeResponseDto(UUID id, String username, String email, Long teamId, String teamName, Set<Role> roles) {
}
