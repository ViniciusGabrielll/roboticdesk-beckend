package com.vinicius.roboticdesk.repository;

import com.vinicius.roboticdesk.entities.Item;
import com.vinicius.roboticdesk.entities.Team;
import com.vinicius.roboticdesk.entities.TeamInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamInviteRepository extends JpaRepository<TeamInvite, UUID> {
    Optional<TeamInvite> findByToken(String token);

    void deleteByTeam(Team team);
}
