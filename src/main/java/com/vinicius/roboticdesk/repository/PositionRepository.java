package com.vinicius.roboticdesk.repository;

import com.vinicius.roboticdesk.entities.Item;
import com.vinicius.roboticdesk.entities.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

}
