    package com.vinicius.roboticdesk.entities;

    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

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

        @Column(unique = true)
        private String username;

        private String password;

        @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        @JoinTable(
                name = "tb_users_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id")
        )
        private Set<Role> roles;

        @ManyToOne
        @JoinColumn(name = "team_id")
        private Team team;

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
    }
