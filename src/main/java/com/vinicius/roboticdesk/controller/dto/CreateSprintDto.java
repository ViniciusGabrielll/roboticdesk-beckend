package com.vinicius.roboticdesk.controller.dto;

import java.time.LocalDate;

public record CreateSprintDto(String title, LocalDate fromTime, LocalDate toTime) {
}
