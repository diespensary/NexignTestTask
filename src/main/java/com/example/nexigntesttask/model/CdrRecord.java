package com.example.nexigntesttask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cdr_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CdrRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "initiator_msisdn", nullable = false)
    private String initiatorMsisdn;

    @Column(name = "recipient_msisdn", nullable = false)
    private String recipientMsisdn;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
}
