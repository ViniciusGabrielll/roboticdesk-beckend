    package com.vinicius.roboticdesk.entities;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.vinicius.roboticdesk.controller.dto.LoginRequest;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;
    import org.springframework.security.crypto.password.PasswordEncoder;

    import java.util.List;
    import java.util.Set;
    import java.util.UUID;

    @Getter
    @Setter
    @Entity
    @Table(name = "tb_users")
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name = "user_id")
        private UUID userId;

        @Column(name = "email", unique = true, nullable = false)
        private String email;

        @Column(unique = true)
        private String username;

        private String password;

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
                name = "tb_users_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id")
        )
        private Set<Role> roles;

        @ManyToOne
        @JoinColumn(name = "team_id")
        private Team team;

        @ManyToMany
        @JoinTable(
                name = "tb_users_positions",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "position_id")
        )
        private List<Position> positions;

        @Getter
        public enum Values {
            ADMIN(1L),
            SCRUMMASTER(2L),
            BASIC(3L);



            long roleId;

            Values(long roleId) {
                this.roleId = roleId;
            }
        }

        public boolean isLoginCorrect(LoginRequest loginRequest, PasswordEncoder passwordEncoder) {
            return passwordEncoder.matches(loginRequest.password(), this.password);
        }
    }
