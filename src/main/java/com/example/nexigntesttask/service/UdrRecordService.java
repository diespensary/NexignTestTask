package com.example.nexigntesttask.service;

import com.example.nexigntesttask.dto.UdrRecordDto;
import com.example.nexigntesttask.model.CdrRecord;
import com.example.nexigntesttask.repository.CdrRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UdrRecordService {
    private final CdrRecordRepository cdrRecordRepository;

    /**
     * Получить UDR для одного абонента
     * Если month передан – агрегировать данные за указанный месяц (год зафиксирован – 2025)
     * Иначе – агрегировать за весь период
     */
    public UdrRecordDto getUdrForSubscriber(String msisdn, Integer month) {
        List<CdrRecord> records;
        if (month != null) {
            LocalDateTime start = getStartForMonth(month);
            LocalDateTime end = getEndForMonth(month);
            // Используем кастомный метод репозитория, который выбирает записи по initiatorMsisdn или recipientMsisdn
            records = cdrRecordRepository.findByMsisdnAndStartTimeBetween(msisdn, start, end);
        } else {
            records = cdrRecordRepository.findAll().stream()
                    .filter(r -> msisdn.equals(r.getInitiatorMsisdn()) || msisdn.equals(r.getRecipientMsisdn()))
                    .collect(Collectors.toList());
        }

        // Суммируем длительность входящих и исходящих вызовов отдельно
        long incomingSeconds = records.stream()
                .filter(r -> "02".equals(r.getType()) && msisdn.equals(r.getRecipientMsisdn()))
                .mapToLong(r -> Duration.between(r.getStartTime(), r.getEndTime()).getSeconds())
                .sum();

        long outcomingSeconds = records.stream()
                .filter(r -> "01".equals(r.getType()) && msisdn.equals(r.getInitiatorMsisdn()))
                .mapToLong(r -> Duration.between(r.getStartTime(), r.getEndTime()).getSeconds())
                .sum();

        return new UdrRecordDto(msisdn, formatDuration(incomingSeconds), formatDuration(outcomingSeconds));
    }

    /**
     * Получить UDR для всех абонентов
     * Если month передан – агрегировать данные за указанный месяц (год зафиксирован – 2025)
     * Иначе – агрегировать за весь период
     */
    public List<UdrRecordDto> getUdrForAllSubscribers(Integer month) {
        List<CdrRecord> records;
        if (month != null) {
            LocalDateTime start = getStartForMonth(month);
            LocalDateTime end = getEndForMonth(month);
            records = cdrRecordRepository.findByStartTimeBetween(start, end);
        } else {
            records = cdrRecordRepository.findAll();
        }

        // Для агрегации по всем абонентам пройдёмся по всем записям и соберем данные в карту,
        // Где ключ – номер абонента, а значение – массив из двух элементов:
        // [0] – суммарное время входящих вызовов, [1] – суммарное время исходящих вызовов
        Map<String, long[]> durations = new java.util.HashMap<>();
        for (CdrRecord record : records) {
            long callDuration = Duration.between(record.getStartTime(), record.getEndTime()).getSeconds();
            // Если это исходящий вызов, обновляем данные инициатора
            if ("01".equals(record.getType())) {
                durations.computeIfAbsent(record.getInitiatorMsisdn(), k -> new long[2]);
                durations.get(record.getInitiatorMsisdn())[1] += callDuration;
            }
            // Если входящий – обновляем данные получателя
            if ("02".equals(record.getType())) {
                durations.computeIfAbsent(record.getRecipientMsisdn(), k -> new long[2]);
                durations.get(record.getRecipientMsisdn())[0] += callDuration;
            }
        }
        // Формируем список DTO на основе собранных данных
        return durations.entrySet().stream()
                .map(e -> new UdrRecordDto(e.getKey(), formatDuration(e.getValue()[0]), formatDuration(e.getValue()[1])))
                .collect(Collectors.toList());
    }

    // Для вычисления начала и конца месяца (год зафиксирован – 2025)
    private LocalDateTime getStartForMonth(int month) {
        return YearMonth.of(2025, month).atDay(1).atStartOfDay();
    }

    private LocalDateTime getEndForMonth(int month) {
        return YearMonth.of(2025, month).atEndOfMonth().atTime(23, 59, 59);
    }

    // Для форматирования секунд в формат hh:mm:ss
    private String formatDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
