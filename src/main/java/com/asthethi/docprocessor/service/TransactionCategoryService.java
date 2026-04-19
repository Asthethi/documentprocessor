package com.asthethi.docprocessor.service;

import com.asthethi.docprocessor.mapper.TrxCategoryMapper;
import com.asthethi.docprocessor.model.TrxCategoryRequest;
import com.asthethi.docprocessor.model.entity.TransactionCategory;
import com.asthethi.docprocessor.reporitory.TransactionCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionCategoryService {

    private TrxCategoryMapper trxCategoryMapper;

    private TransactionCategoryRepository transactionCategoryRepository;

    @Transactional
    public List<TransactionCategory> findAllCategories() {
       return transactionCategoryRepository.findAllWithKeywords();
    }

    public TransactionCategory saveTransactionCategory(TrxCategoryRequest trxCategoryRequest) {
        return transactionCategoryRepository
                .save(trxCategoryMapper.toTransactionCategory(trxCategoryRequest));
    }

    @Transactional
    public void deleteTransactionCategoryByName(String name) {
        transactionCategoryRepository.deleteByName(name);
    }
}
