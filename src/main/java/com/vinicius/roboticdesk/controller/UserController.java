package com.vinicius.roboticdesk.controller;

import com.vinicius.roboticdesk.controller.dto.CreateUserDto;
import com.vinicius.roboticdesk.controller.dto.MeResponseDto;
import com.vinicius.roboticdesk.entities.Role;
import com.vinicius.roboticdesk.entities.User;
import com.vinicius.roboticdesk.repository.RoleRepository;
import com.vinicius.roboticdesk.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;


    @Transactional
    @PostMapping("/users")
    public ResponseEntity<Void> newUser(@RequestBody CreateUserDto dto) {

        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        var userFromDb = userRepository.findByUsername(dto.username());

         if(userFromDb.isPresent()) {
             throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
         }

        userFromDb = userRepository.findByEmail(dto.email());

        if(userFromDb.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

         var user = new User();
         user.setUsername(dto.username());
         user.setEmail(dto.email());
         user.setPassword(passwordEncoder.encode(dto.password()));
         user.setRoles(Set.of(basicRole));

         userRepository.save(user);

         return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<User>> listUsers() {
        var users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("user/me")
    public MeResponseDto me(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return new MeResponseDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getTeam() != null ? user.getTeam().getId() : null,
                user.getTeam() != null ? user.getTeam().getName() : null
        );
    }

    @PreAuthorize("hasRole('admin')")
    @Transactional
    @PostMapping("/admin/user")
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDto dto) {

        var scrumRole = roleRepository.findByName(Role.Values.SCRUMMASTER.name());

        var userFromDb = userRepository.findByUsername(dto.username());

        if(userFromDb.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        userFromDb = userRepository.findByEmail(dto.email());

        if(userFromDb.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(Set.of(scrumRole));

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("users/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {

        UUID uuid = UUID.fromString(
                id.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"
                )
        );

        if (!userRepository.existsById(uuid)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuário não encontrado"
            );
        }

        userRepository.deleteById(uuid);

        return ResponseEntity.noContent().build();
    }
}
