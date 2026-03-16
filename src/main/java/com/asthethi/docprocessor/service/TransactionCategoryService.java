package com.asthethi.docprocessor.service;

import com.asthethi.docprocessor.mapper.TrxCategoryMapper;
import com.asthethi.docprocessor.model.TrxCategoryRequest;
import com.asthethi.docprocessor.model.entity.TransactionCategory;
import com.asthethi.docprocessor.reporitory.TransactionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionCategoryService {

    @Autowired
    private TrxCategoryMapper trxCategoryMapper;

    @Autowired
    private TransactionCategoryRepository transactionCategoryRepository;

    public List<TransactionCategory> findAllCategories() {
       return transactionCategoryRepository.findAll();
    }

    public TransactionCategory saveTransactionCategory(TrxCategoryRequest trxCategoryRequest) {
        return transactionCategoryRepository
                .save(trxCategoryMapper.toTransactionCategory(trxCategoryRequest));
    }
}
