package com.example.nexigntesttask;

import com.example.nexigntesttask.dto.UdrResponse;
import com.example.nexigntesttask.model.CdrRecord;
import com.example.nexigntesttask.model.Subscriber;
import com.example.nexigntesttask.repository.CdrRecordRepository;
import com.example.nexigntesttask.repository.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UdrControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private CdrRecordRepository cdrRecordRepository;

    private final String testMsisdn = "79991112233";
    private final String anotherMsisdn = "79992223344";

    @BeforeEach
    void setup() {
        subscriberRepository.deleteAll();
        cdrRecordRepository.deleteAll();

        // Создание тестовых абонентов
        Subscriber subscriber1 = new Subscriber();
        subscriber1.setMsisdn(testMsisdn);
        subscriberRepository.save(subscriber1);

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setMsisdn(anotherMsisdn);
        subscriberRepository.save(subscriber2);

        // Создание тестовых CDR-записей
        CdrRecord outgoingCall = new CdrRecord();
        outgoingCall.setType("01");
        outgoingCall.setInitiatorMsisdn(testMsisdn);
        outgoingCall.setRecipientMsisdn(anotherMsisdn);
        outgoingCall.setStartTime(LocalDateTime.of(2025, 2, 1, 10, 0));
        outgoingCall.setEndTime(LocalDateTime.of(2025, 2, 1, 10, 5)); // 5 минут
        cdrRecordRepository.save(outgoingCall);

        CdrRecord incomingCall = new CdrRecord();
        incomingCall.setType("02");
        incomingCall.setInitiatorMsisdn(anotherMsisdn);
        incomingCall.setRecipientMsisdn(testMsisdn);
        incomingCall.setStartTime(LocalDateTime.of(2025, 3, 1, 12, 0));
        incomingCall.setEndTime(LocalDateTime.of(2025, 3, 1, 12, 2, 30)); // 2.5 минуты
        cdrRecordRepository.save(incomingCall);
    }

    // Тест 1: Получение UDR для абонента с фильтром по месяцу
    @Test
    void getUdrForSubscriber_WithValidMonth_ReturnsAggregatedData() {
        UdrResponse response = restTemplate.getForObject(
                "/udr?msisdn={msisdn}&month=2",
                UdrResponse.class,
                testMsisdn
        );

        assertNotNull(response);
        assertEquals(testMsisdn, response.msisdn());
        assertEquals("00:05:00", response.outcomingCallTotalTime()); // 5 минут исходящих
        assertEquals("00:00:00", response.incomingCallTotalTime()); // Входящих в феврале нет
    }

    // Тест 2: Получение UDR для абонента без фильтрации по месяцу
    @Test
    void getUdrForSubscriber_WithoutMonth_ReturnsAllData() {
        UdrResponse response = restTemplate.getForObject(
                "/udr?msisdn={msisdn}",
                UdrResponse.class,
                testMsisdn
        );

        assertEquals("00:05:00", response.outcomingCallTotalTime());
        assertEquals("00:02:30", response.incomingCallTotalTime()); // Включает мартовский вызов
    }

    // Тест 3: Получение UDR для всех абонентов
    @Test
    void getUdrForAllSubscribers_ReturnsAllAggregatedData() {
        List<UdrResponse> response = restTemplate.getForObject(
                "/udr/all",
                List.class
        );

        assertEquals(2, response.size()); // Два абонента
    }

    @Test
    void getUdrForSubscriber_InvalidMonth_ThrowsException() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/udr?msisdn={msisdn}&month=13",
                String.class,
                testMsisdn
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid month"));
    }

    @Test
    void getUdrForSubscriber_NoCdrRecords_ReturnsZeroDurations() {
        // Создаем нового абонента без CDR-записей
        String newMsisdn = "79994445566";
        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn(newMsisdn);
        subscriberRepository.save(subscriber);

        UdrResponse response = restTemplate.getForObject(
                "/udr?msisdn={msisdn}",
                UdrResponse.class,
                newMsisdn
        );

        assertEquals("00:00:00", response.incomingCallTotalTime());
        assertEquals("00:00:00", response.outcomingCallTotalTime());
    }

    @Test
    void getUdrForSubscriber_MissingMsisdn_ReturnsBadRequest() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/udr?month=2",
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUdrForSubscriber_InvalidMonthType_ReturnsBadRequest() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/udr?msisdn={msisdn}&month=invalid",
                String.class,
                testMsisdn
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
