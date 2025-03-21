package com.example.nexigntesttask.repository;

import com.example.nexigntesttask.model.CdrRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CdrRecordRepository extends JpaRepository<CdrRecord, Long> {
    List<CdrRecord> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT c FROM CdrRecord c WHERE c.startTime BETWEEN :start AND :end AND (c.initiatorMsisdn = :msisdn OR c.recipientMsisdn = :msisdn)")
    List<CdrRecord> findByMsisdnAndStartTimeBetween(@Param("msisdn") String msisdn, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
