package com.example.nexigntesttask.services;

import com.example.nexigntesttask.models.Subscriber;
import com.example.nexigntesttask.repositories.SubscriberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class SubscriberGeneratorService {
    private final SubscriberRepository subscriberRepository;
    private final Random random = new Random();

    @Transactional
    public void initSubscribers(int count) {
        if (count < 2) {
            throw new IllegalArgumentException("Количество абонентов должно быть не менее 2");
        }

        long currentCount = subscriberRepository.count();

        if (currentCount != count) {
            subscriberRepository.deleteAll();
        } else {
            return;
        }

        // диапазон российских номеров
        long min = 79000000000L;
        long max = 79999999999L;
        long range = max - min + 1; // генерируем случайный номер так: прибавляем к min число от 0 до range - 1

        for (int i = 0; i < count; i++) {
            String msisdn;
            boolean isUnique;
            do {
                long randomNumber = min + (Math.abs(random.nextLong()) % range);
                msisdn = Long.toString(randomNumber);

                isUnique = subscriberRepository.existsByMsisdn(msisdn);
            } while (isUnique);

            Subscriber subscriber = new Subscriber();
            subscriber.setMsisdn(msisdn);
            subscriberRepository.save(subscriber);
        }
    }
}
