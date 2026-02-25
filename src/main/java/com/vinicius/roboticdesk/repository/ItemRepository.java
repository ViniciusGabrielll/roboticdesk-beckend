package com.vinicius.roboticdesk.repository;

import com.vinicius.roboticdesk.entities.Item;
import com.vinicius.roboticdesk.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    void deleteByTeam(Team team);
}
