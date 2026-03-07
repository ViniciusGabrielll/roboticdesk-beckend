package com.vinicius.roboticdesk.controller;

import com.vinicius.roboticdesk.controller.dto.CreateItemDto;
import com.vinicius.roboticdesk.controller.dto.CreateSprintDto;
import com.vinicius.roboticdesk.entities.*;
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

    private final ItemRepository itemRepository;

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
        sprint.setTitle(dto.title());
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

    @Transactional
    @DeleteMapping("/{sprintId}")
    public ResponseEntity<Void> deleteSprint(@PathVariable Long sprintId, @AuthenticationPrincipal Jwt
            jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuário não encontrado"
                ));

        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Sprint não encontrado"
        ));

        if(!user.getTeam().getSprints().contains(sprint)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Essa sprint não pertence ao seu time.");
        }

        for (Item item : sprint.getItems()) {
            item.setSprint(null);
        }
        sprint.getItems().clear();

        sprintRepository.delete(sprint);

        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/{sprintId}/items/{itemId}")
    public ResponseEntity<Void> assignItemSprint(@PathVariable Long sprintId, @PathVariable Long itemId, @AuthenticationPrincipal Jwt
            jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuário não encontrado"
                ));

        Sprint sprint = sprintRepository.findById(sprintId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Sprint não encontrado"));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Item não encontrado"));


        if(!user.getTeam().getSprints().contains(sprint)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Essa sprint não pertence ao seu time.");
        }
        if(!user.getTeam().getItems().contains(item)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esse item não pertence ao seu time.");
        }

        sprint.getItems().add(item);
        item.setSprint(sprint);
        item.setStatus(ItemStatus.TODO);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
