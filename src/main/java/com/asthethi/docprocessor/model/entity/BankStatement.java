package com.asthethi.docprocessor.model.entity;

import com.asthethi.docprocessor.model.TransactionResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "banks_tatement")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BankStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "bankStatement", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
    private String bankName;
}
