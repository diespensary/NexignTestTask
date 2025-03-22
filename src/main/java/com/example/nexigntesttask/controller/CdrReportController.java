package com.example.nexigntesttask.controller;

import com.example.nexigntesttask.service.CdrReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    @GetMapping
    public ResponseEntity<Map<String, String>> generateCdrReport(
            @RequestParam String msisdn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            UUID reportUuid = cdrReportService.generateReport(msisdn, startDate, endDate);
            Map<String, String> response = new HashMap<>();
            response.put("message", "CDR report generated successfully. File is ready in /reports directory.");
            response.put("uuid", reportUuid.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error generating CDR report: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
