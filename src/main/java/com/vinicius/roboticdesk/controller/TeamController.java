package com.vinicius.roboticdesk.controller;

import com.vinicius.roboticdesk.controller.dto.CreateTeamDto;
import com.vinicius.roboticdesk.entities.Role;
import com.vinicius.roboticdesk.entities.Team;
import com.vinicius.roboticdesk.entities.User;
import com.vinicius.roboticdesk.repository.RoleRepository;
import com.vinicius.roboticdesk.repository.TeamRepository;
import com.vinicius.roboticdesk.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/teams")
public class TeamController {


    private final TeamRepository teamRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    @PreAuthorize("hasRole('admin') or hasRole('scrummaster')")
    @Transactional
    @PostMapping
    public ResponseEntity<Void> createTeam(@RequestBody CreateTeamDto dto, @AuthenticationPrincipal Jwt
            jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuário não encontrado"
                ));

        if(user.getTeam() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já pertence a um time");
        }

        var team = new Team();
        team.setName(dto.name());
        teamRepository.save(team);
        user.setTeam(team);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('admin') or hasRole('scrummaster')")
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id, @AuthenticationPrincipal Jwt
            jwt) {

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Time não encontrado"
                ));

        UUID userId = UUID.fromString(jwt.getSubject());

        User userRequest = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Seu usuário não encontrado"
                ));

        List<User> users = userRepository.findAllByTeam(team);

        boolean isAdmin = userRequest.getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        boolean belongsToTeam = userRequest.getTeam() != null
                && userRequest.getTeam().getId().equals(team.getId());

        if (!belongsToTeam && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pertence a esse time");
        }

        for (User user : users) {
            user.setTeam(null);
        }

        teamRepository.delete(team);


        return ResponseEntity.noContent().build();
    }
}
