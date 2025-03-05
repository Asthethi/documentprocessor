package com.asthethi.docprocessor.mapper;

import com.asthethi.docprocessor.model.Transaction;
import com.asthethi.docprocessor.model.TransactionResponse;
import org.springframework.stereotype.Service;

@Service
public class TransactionMapper {
    public TransactionResponse toTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .narration(transaction.getNarration())
                .transactionType(transaction.getTransactionType())
                .transactionDate(transaction.getTransactionDate())
                .debitAmount(transaction.getDebitAmount())
                .creditAmount(transaction.getCreditAmount())
                .refNumber(transaction.getRefNumber())
                .closingBalance(transaction.getClosingBalance())
                .build();
    }
}
