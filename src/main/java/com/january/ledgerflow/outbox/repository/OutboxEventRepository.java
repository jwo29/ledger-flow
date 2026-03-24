package com.january.ledgerflow.outbox.repository;

import com.january.ledgerflow.outbox.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Integer> {

    List<OutboxEvent> findTop100ByStatus(String status);

}
