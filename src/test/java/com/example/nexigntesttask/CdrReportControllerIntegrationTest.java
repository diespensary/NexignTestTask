package com.example.nexigntesttask;

import com.example.nexigntesttask.model.CdrRecord;
import com.example.nexigntesttask.model.Subscriber;
import com.example.nexigntesttask.repository.CdrRecordRepository;
import com.example.nexigntesttask.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RequiredArgsConstructor
public class CdrReportControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private CdrRecordRepository cdrRecordRepository;

    private final String testMsisdn = "79991112233";

    @BeforeEach
    void setup() {
        subscriberRepository.deleteAll();
        cdrRecordRepository.deleteAll();

        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn(testMsisdn);
        subscriberRepository.save(subscriber);

        CdrRecord record = new CdrRecord();
        record.setType("01");
        record.setInitiatorMsisdn(testMsisdn);
        record.setRecipientMsisdn("79993334455");
        record.setStartTime(LocalDateTime.of(2025, 2, 1, 10, 0));
        record.setEndTime(LocalDateTime.of(2025, 2, 1, 10, 5));
        cdrRecordRepository.save(record);
    }

    // Тест 1: Успешная генерация отчета
    @Test
    void generateCdrReport_ValidRequest_ReturnsUUIDAndCreatesFile() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/cdr-report?msisdn={msisdn}&startDate=2025-02-01T00:00:00&endDate=2025-02-28T23:59:59",
                Map.class,
                testMsisdn
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("uuid"));

        String uuid = (String) response.getBody().get("uuid");
        File reportFile = new File("reports/" + testMsisdn + "_" + uuid + ".csv");
        assertTrue(reportFile.exists());
        reportFile.delete();
    }

    // Тест 2: Некорректный MSISDN
    @Test
    void generateCdrReport_InvalidMsisdn_ReturnsError() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/cdr-report?msisdn=invalid&startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59",
                Map.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error generating CDR report"));
    }

    // Тест 3: Отчет за период без данных
    @Test
    void generateCdrReport_NoDataInPeriod_ReturnsEmptyFile() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/cdr-report?msisdn={msisdn}&startDate=2026-01-01T00:00:00&endDate=2026-01-02T00:00:00",
                Map.class,
                testMsisdn
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        String uuid = (String) response.getBody().get("uuid");
        File reportFile = new File("reports/" + testMsisdn + "_" + uuid + ".csv");
        assertEquals(0, reportFile.length()); // Пустой файл
        reportFile.delete();
    }
}
