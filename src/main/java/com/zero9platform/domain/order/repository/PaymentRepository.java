package com.zero9platform.domain.order.repository;

import com.zero9platform.domain.order.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
