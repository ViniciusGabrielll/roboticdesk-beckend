package com.vinicius.roboticdesk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long positionId;

    @Column(unique = true)
    private String positionName;

    @ManyToMany(mappedBy = "positions")
    private List<User> users;

    @ManyToMany(mappedBy = "positions")
    private List<Item> items;

    @ManyToOne
    private Team team;

}
