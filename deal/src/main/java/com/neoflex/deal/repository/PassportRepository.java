package com.neoflex.deal.repository;

import com.neoflex.deal.entity.jsonb.element.Passport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassportRepository extends JpaRepository<Passport, Long> {
}
