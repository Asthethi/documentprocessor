package com.asthethi.docprocessor.mapper;

import com.asthethi.docprocessor.model.Transaction;
import com.asthethi.docprocessor.model.TransactionResponse;
import com.asthethi.docprocessor.model.entity.BankStatement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<com.asthethi.docprocessor.model.entity.Transaction> toTransactionList(List<TransactionResponse> transactionResponseList) {

        List<com.asthethi.docprocessor.model.entity.Transaction> transactions = new ArrayList<>();

        transactionResponseList.forEach(transaction -> {
            transactions.add(com.asthethi.docprocessor.model.entity.Transaction.builder().transactionDate(transaction.getTransactionDate())
                    .transactionCategory(transaction.getTransactionCategory())
                    .transactionType(transaction.getTransactionType())
                    .narration(transaction.getNarration())
                    .refNumber(transaction.getRefNumber())
                    .debitAmount(transaction.getDebitAmount())
                    .creditAmount(transaction.getCreditAmount())
                    .closingBalance(transaction.getClosingBalance())
                    .build());
        });

        return transactions;
    }

    public BankStatement fromTransactionResponse(List<com.asthethi.docprocessor.model.entity.Transaction> transactions, String bankName) {
        return BankStatement.builder()
                .transactions(transactions)
                .bankName(bankName)
                .build();
    }
}
