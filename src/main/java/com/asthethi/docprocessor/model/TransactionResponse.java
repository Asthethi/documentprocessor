package com.asthethi.docprocessor.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class TransactionResponse {
    private String transactionDate;
    private String narration;
    private Double debitAmount;
    private Double creditAmount;
    private String refNumber;
    private String closingBalance;
    private String transactionType;
}
