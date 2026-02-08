package com.vinicius.roboticdesk.controller;

import com.vinicius.roboticdesk.controller.dto.CreateUserDto;
import com.vinicius.roboticdesk.entities.Role;
import com.vinicius.roboticdesk.entities.User;
import com.vinicius.roboticdesk.repository.RoleRepository;
import com.vinicius.roboticdesk.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

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

         var user = new User();
         user.setUsername(dto.username());
         user.setPassword(passwordEncoder.encode(dto.password()));
         user.setRoles(Set.of(basicRole));

         userRepository.save(user);

         return ResponseEntity.ok().build();
    }
}
