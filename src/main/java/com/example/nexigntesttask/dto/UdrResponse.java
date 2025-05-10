package com.example.nexigntesttask.dto;


public record UdrResponse(
        String msisdn,
        String incomingCallTotalTime,
        String outcomingCallTotalTime
) {
}
