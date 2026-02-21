package com.vinicius.roboticdesk.controller.dto;

import java.util.UUID;

public record MeResponseDto(UUID id, String username, String email, Long teamId, String teamName) {
}
