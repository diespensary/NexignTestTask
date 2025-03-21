package com.example.nexigntesttask.controller;

import com.example.nexigntesttask.dto.UdrRecordDto;
import com.example.nexigntesttask.service.UdrRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/udr")
@RequiredArgsConstructor
public class UdrController {
    private final UdrRecordService udrService;

    /**
     * Получить UDR для конкретного абонента.
     * Параметры:
     * - msisdn – номер абонента (обязательный)
     * - month – номер месяца (от 1 до 12). Если параметр отсутствует, агрегируются данные за весь период.
     * Пример запроса:
     * GET /udr?msisdn=79990000001&month=2
     */
    @GetMapping
    public UdrRecordDto getUdrForSubscriber(
            @RequestParam String msisdn,
            @RequestParam(required = false) Integer month) {
        return udrService.getUdrForSubscriber(msisdn, month);
    }

    /**
     * Получить UDR для всех абонентов.
     * Параметры:
     * - month – номер месяца (от 1 до 12). Если параметр отсутствует, агрегируются данные за весь период.
     * Пример запроса:
     * GET /udr/all?month=2
     */
    @GetMapping("/all")
    public List<UdrRecordDto> getUdrForAllSubscribers(
            @RequestParam(required = false) Integer month) {
        return udrService.getUdrForAllSubscribers(month);
    }
}
