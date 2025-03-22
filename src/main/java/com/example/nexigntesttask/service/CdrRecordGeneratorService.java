package com.example.nexigntesttask.service;

import com.example.nexigntesttask.model.CdrRecord;
import com.example.nexigntesttask.model.Subscriber;
import com.example.nexigntesttask.repository.CdrRecordRepository;
import com.example.nexigntesttask.repository.SubscriberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CdrRecordGeneratorService {
    private final SubscriberRepository subscriberRepository;
    private final CdrRecordRepository cdrRecordRepository;
    private final Random random = new Random();

    @Transactional
    public void generateCdrRecordsForOneYear() {
        List<Subscriber> subscribers = subscriberRepository.findAll();

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        LocalDateTime currentTime = startDate;

        List<CdrRecord> records = new ArrayList<>();

        while (currentTime.isBefore(endDate)) {
            // Интервал между звонками от 30 минут до 7 дней
            int intervalBetweenCalls = 30 + random.nextInt(7 * 24 * 60 - 30);
            currentTime = currentTime.plusMinutes(intervalBetweenCalls);
            if (currentTime.isAfter(endDate)) {
                break;
            }

            // Длительность звонка от 30 секунд до 30 минут
            int durationSeconds = 30 + random.nextInt(30 * 60 - 30);
            LocalDateTime callEndTime = currentTime.plusSeconds(durationSeconds);

            Subscriber ourSubscriber = subscribers.get(random.nextInt(subscribers.size()));

            Subscriber otherSubscriber;
            do {
                otherSubscriber = subscribers.get(random.nextInt(subscribers.size()));
            } while (otherSubscriber.getId().equals(ourSubscriber.getId()));

            boolean isOutgoing = random.nextBoolean();
            String callType = isOutgoing ? "01" : "02";
            String initiatingNumber = isOutgoing ? ourSubscriber.getMsisdn() : otherSubscriber.getMsisdn();
            String receivingNumber = isOutgoing ? otherSubscriber.getMsisdn() : ourSubscriber.getMsisdn();

            CdrRecord record = new CdrRecord();
            record.setType(callType);
            record.setInitiatorMsisdn(initiatingNumber);
            record.setRecipientMsisdn(receivingNumber);
            record.setStartTime(currentTime);
            record.setEndTime(callEndTime);

            records.add(record);
        }

        cdrRecordRepository.saveAll(records);
    }
}
