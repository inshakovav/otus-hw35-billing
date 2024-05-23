package com.example.billing.repository;

import com.example.billing.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findFirstByOrderByIdDesc();
    Optional<PaymentEntity> findFirstByOrderId(Long orderId);
}
