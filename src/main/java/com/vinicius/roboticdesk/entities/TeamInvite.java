package com.vinicius.roboticdesk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class TeamInvite {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne
    private Team team;

    private LocalDateTime expiresAt;
    private int maxUses;
    private int usedCount;

    private boolean active;
}
