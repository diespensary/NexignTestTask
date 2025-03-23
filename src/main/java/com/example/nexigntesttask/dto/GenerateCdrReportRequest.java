package com.example.nexigntesttask.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record GenerateCdrReportRequest(
        String msisdn,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
) {
}
