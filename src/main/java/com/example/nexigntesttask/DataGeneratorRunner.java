package com.example.nexigntesttask;

import com.example.nexigntesttask.services.CdrGeneratorService;
import com.example.nexigntesttask.services.SubscriberGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataGeneratorRunner implements ApplicationRunner {
    private final SubscriberGeneratorService subscriberService;
    private final CdrGeneratorService callGeneratorService;

    @Override
    public void run(ApplicationArguments args) {
        subscriberService.initSubscribers(20);
        callGeneratorService.generateCdrRecordsForOneYear();
        System.out.println("Генерация абонентов и CDR записей завершена.");
    }
}
