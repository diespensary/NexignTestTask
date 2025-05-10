package com.example.nexigntesttask.service;

import com.example.nexigntesttask.handler.exceptions.FileWriteException;
import com.example.nexigntesttask.model.CdrRecord;
import com.example.nexigntesttask.repository.CdrRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CdrReportService {
    private final CdrRecordRepository cdrRecordRepository;

    /**
     * Генерирует CDR-отчет для указанного абонента за заданный период.
     *
     * @param msisdn    номер абонента
     * @param startDate начало периода
     * @param endDate   конец периода
     * @return уникальный UUID запроса
     * @throws IOException если возникает ошибка при записи файла
     */
    public UUID generateReport(String msisdn, LocalDateTime startDate, LocalDateTime endDate) {
        // Выбираем все записи, где номер встречается как инициатор или получатель, и время звонка входит в заданный период
        List<CdrRecord> records = cdrRecordRepository.findAll().stream()
                .filter(r ->
                        (msisdn.equals(r.getInitiatorMsisdn()) || msisdn.equals(r.getRecipientMsisdn())) &&
                                (r.getStartTime().isEqual(startDate) || r.getStartTime().isAfter(startDate)) &&
                                (r.getStartTime().isEqual(endDate) || r.getStartTime().isBefore(endDate))
                )
                .sorted(Comparator.comparing(CdrRecord::getStartTime))
                .toList();

        // Генерируем уникальный UUID для данного запроса
        UUID reportUuid = UUID.randomUUID();

        // Гарантируем, что директория /reports существует
        String reportsDirPath = "reports";
        File reportsDir = new File(reportsDirPath);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        // Формируем имя файла: {msisdn}_{UUID}.csv
        String filename = msisdn + "_" + reportUuid.toString() + ".csv";
        File reportFile = new File(reportsDir, filename);

        // Используем формат ISO для дат
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
            // Для каждой записи записываем строку: тип, initiatorMsisdn, recipientMsisdn, startTime, endTime
            // Разделитель – запятая, записи разделяются переносом строки
            for (CdrRecord record : records) {
                String line = String.join(", ",
                        record.getType(),
                        record.getInitiatorMsisdn(),
                        record.getRecipientMsisdn(),
                        record.getStartTime().format(formatter),
                        record.getEndTime().format(formatter)
                );
                writer.write(line);
                writer.newLine();
            }
        }
        catch (IOException e) {
            throw new FileWriteException();
        }

        return reportUuid;
    }
}
