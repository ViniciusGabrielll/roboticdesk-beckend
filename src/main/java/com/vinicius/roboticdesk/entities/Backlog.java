package com.vinicius.roboticdesk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_backlogs")
public class Backlog {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "backlog_id")
    private Long backlogId;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}
