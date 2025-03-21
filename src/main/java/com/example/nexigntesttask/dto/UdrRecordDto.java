package com.example.nexigntesttask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UdrRecordDto {
    private String msisdn;
    private String incomingCallTotalTime;
    private String outcomingCallTotalTime;
}
