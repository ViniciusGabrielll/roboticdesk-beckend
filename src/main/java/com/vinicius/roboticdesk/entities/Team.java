package com.vinicius.roboticdesk.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "team")
    private List<User> users;

    @OneToMany(mappedBy = "team")
    private List<Sprint> sprints;

    @OneToMany(mappedBy = "team")
    private List<Item> items;

    @OneToMany(mappedBy = "team")
    private List<Position> positions;

}
