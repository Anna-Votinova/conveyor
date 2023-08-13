package com.neoflex.deal.repository;

import com.neoflex.deal.entity.jsonb.element.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
}
