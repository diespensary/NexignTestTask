package com.example.nexigntesttask.repositories;

import com.example.nexigntesttask.models.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    boolean existsByMsisdn(String msisdn);
}
