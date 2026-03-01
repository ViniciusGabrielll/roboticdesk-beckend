package com.vinicius.roboticdesk.controller.dto;

import java.time.LocalDate;

public record CreateSprintDto(LocalDate fromTime, LocalDate toTime) {
}
