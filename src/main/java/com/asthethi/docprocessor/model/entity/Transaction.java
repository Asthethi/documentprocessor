package com.asthethi.docprocessor.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionDate;
    private String narration;
    private Double debitAmount;
    private Double creditAmount;
    private String refNumber;
    private String closingBalance;
    private String transactionType;
    private String transactionCategory;

    @ManyToOne
    @JoinColumn(name = "bank_statement_id")
    private BankStatement bankStatement;
}
