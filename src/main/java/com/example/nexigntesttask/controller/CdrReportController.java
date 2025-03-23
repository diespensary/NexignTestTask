package com.example.nexigntesttask.controller;

import com.example.nexigntesttask.dto.CdrReportResponse;
import com.example.nexigntesttask.dto.GenerateCdrReportRequest;
import com.example.nexigntesttask.dto.UdrResponse;
import com.example.nexigntesttask.service.CdrReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cdr-report")
@RequiredArgsConstructor
public class CdrReportController {
    private final CdrReportService cdrReportService;

    /**
     * REST метод для генерации CDR-отчета.
     * Параметры:
     * - msisdn: номер абонента
     * - startDate, endDate: границы периода (формат ISO 8601)
     *
     * Пример запроса:
     * POST /cdr-report?msisdn=79991112233&startDate=2025-02-01T00:00:00&endDate=2025-02-15T23:59:59
     *
     * Возвращает сообщение и уникальный UUID запроса.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CdrReportResponse generateCdrReport(@RequestBody GenerateCdrReportRequest generateCdrReportRequest) {
        UUID reportUuid = cdrReportService.generateReport(
                generateCdrReportRequest.msisdn(),
                generateCdrReportRequest.startDate(),
                generateCdrReportRequest.endDate()
        );

        return new CdrReportResponse(reportUuid);
    }
}
