package com.vinicius.roboticdesk.config;

import com.vinicius.roboticdesk.entities.Role;
import com.vinicius.roboticdesk.entities.User;
import com.vinicius.roboticdesk.repository.RoleRepository;
import com.vinicius.roboticdesk.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
@AllArgsConstructor
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

        var userAdmin = userRepository.findByEmail("admin@gmail.com");

        userAdmin.ifPresentOrElse(
                user -> {System.out.println("admin já existe"); },
                () -> {
                    var user = new User();
                    user.setUsername("RoboticDeskAdmin");
                    user.setEmail("admin@gmail.com");
                    user.setPassword(passwordEncoder.encode("2vaieg01"));
                    user.setRoles(Set.of(roleAdmin));
                    userRepository.save(user);


                }
        );
    }
}
