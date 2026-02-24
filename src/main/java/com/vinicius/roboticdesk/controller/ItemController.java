package com.vinicius.roboticdesk.controller;

import com.vinicius.roboticdesk.controller.dto.CreateItemDto;
import com.vinicius.roboticdesk.entities.Item;
import com.vinicius.roboticdesk.entities.Team;
import com.vinicius.roboticdesk.entities.User;
import com.vinicius.roboticdesk.repository.ItemRepository;
import com.vinicius.roboticdesk.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Transactional
    @PostMapping
    public ResponseEntity<Void> createItem(@RequestBody CreateItemDto dto, @AuthenticationPrincipal Jwt
            jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuário não encontrado"
                ));

        var item = new Item();
        item.setTitle(dto.title());
        item.setTeam(user.getTeam());
        item.setPriority(dto.priority());
        itemRepository.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    @GetMapping
    public ResponseEntity<List<Item>> listItems(@AuthenticationPrincipal Jwt
            jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuário não encontrado"
                ));

        if(user.getTeam() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não pertence a um time");
        }

        var team = new Team();
        team = user.getTeam();

        var items = team.getItems();

        return ResponseEntity.ok(items);
    }
}
