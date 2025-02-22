package com.asthethi.docprocessor.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    private String transactionDate;
    private String narration;
    private Double debitAmount;
    private Double creditAmount;
    private String refNumber;
    private String closingBalance;
    private String transactionType;
}
