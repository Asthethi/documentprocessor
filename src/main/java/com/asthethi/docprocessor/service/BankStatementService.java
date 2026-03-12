package com.asthethi.docprocessor.service;

import com.asthethi.docprocessor.mapper.TransactionMapper;
import com.asthethi.docprocessor.model.TransactionResponse;
import com.asthethi.docprocessor.model.entity.BankStatement;
import com.asthethi.docprocessor.model.entity.Transaction;
import com.asthethi.docprocessor.reporitory.StatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankStatementService {

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    public List<BankStatement> fetchAllBankStatements() {
        return this.statementRepository.findAll();
    }

    public BankStatement saveStatement(List<TransactionResponse> request, String bankName) {
        List<Transaction> transactions = transactionMapper.toTransactionList(request);
        BankStatement statement = transactionMapper.fromTransactionResponse(transactions, bankName);
        return statementRepository.save(statement);
    }

}
