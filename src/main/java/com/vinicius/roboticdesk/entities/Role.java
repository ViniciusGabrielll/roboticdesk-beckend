package com.vinicius.roboticdesk.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "tb_roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    private String name;

    @AllArgsConstructor
    @Getter
    public enum Values {
        ADMIN(1L),
        SCRUMMASTER(2L),
        BASIC(3L);

        long roleId;

    }
}
