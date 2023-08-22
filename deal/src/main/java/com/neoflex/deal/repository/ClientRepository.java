package com.neoflex.deal.repository;

import com.neoflex.deal.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
