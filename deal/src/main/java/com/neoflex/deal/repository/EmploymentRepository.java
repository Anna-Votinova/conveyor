package com.neoflex.deal.repository;

import com.neoflex.deal.entity.jsonb.element.Employment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmploymentRepository extends JpaRepository<Employment, Long> {
}
