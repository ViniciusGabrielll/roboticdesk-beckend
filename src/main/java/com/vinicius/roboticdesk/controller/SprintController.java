package com.vinicius.roboticdesk.controller;

import com.vinicius.roboticdesk.controller.dto.CreateItemDto;
import com.vinicius.roboticdesk.controller.dto.CreateSprintDto;
import com.vinicius.roboticdesk.entities.Item;
import com.vinicius.roboticdesk.entities.Sprint;
import com.vinicius.roboticdesk.entities.Team;
import com.vinicius.roboticdesk.entities.User;
import com.vinicius.roboticdesk.repository.ItemRepository;
import com.vinicius.roboticdesk.repository.SprintRepository;
import com.vinicius.roboticdesk.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/sprints")
public class SprintController {

    private final SprintRepository sprintRepository;

    private final UserRepository userRepository;

    @Transactional
    @PostMapping
    public ResponseEntity<Void> createSprint(@RequestBody CreateSprintDto dto, @AuthenticationPrincipal Jwt
            jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuário não encontrado"
                ));

        Team team = user.getTeam();
        Sprint sprint = new Sprint();
        sprint.setTeam(team);

        if(!dto.fromTime().isBefore(dto.toTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de início é maior do que a de final");
        }
        sprint.setFromTime(dto.fromTime());
        sprint.setToTime(dto.toTime());

        sprintRepository.save(sprint);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    @GetMapping
    public ResponseEntity<List<Sprint>> sprints(@AuthenticationPrincipal Jwt
            jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuário não encontrado"
                ));

        Team team = user.getTeam();

        return ResponseEntity.ok(team.getSprints());
    }

}
