package com.january.ledgerflow.messaging.repository;

import com.january.ledgerflow.messaging.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
    boolean existsById(String id);
}
