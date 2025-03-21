package com.example.nexigntesttask.repository;

import com.example.nexigntesttask.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    boolean existsByMsisdn(String msisdn);
}
