package com.asthethi.docprocessor.mapper;

import com.asthethi.docprocessor.model.TrxCategoryRequest;
import com.asthethi.docprocessor.model.entity.TransactionCategory;
import org.springframework.stereotype.Service;

@Service
public class TrxCategoryMapper {

    public TransactionCategory toTransactionCategory(TrxCategoryRequest trxCategoryRequest) {
        return TransactionCategory.builder().name(trxCategoryRequest.getName())
                .keywords(trxCategoryRequest.getKeywords())
                .build();
    }
}
