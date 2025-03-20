package com.example.nexigntesttask.repositories;

import com.example.nexigntesttask.models.CdrRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdrRecordRepository extends JpaRepository<CdrRecord, Long> {
}
