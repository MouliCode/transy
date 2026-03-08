package com.transfer.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferSessionJpaRepository extends JpaRepository<TransferSessionEntity, String> {
}
