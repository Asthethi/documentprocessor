package com.asthethi.docprocessor.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class TransactionResponse {
    private String transactionDate;
    private String narration;
    private Double debitAmount;
    private Double creditAmount;
    private String refNumber;
    private String closingBalance;
    private String transactionType;
    private String transactionCategory;
}
