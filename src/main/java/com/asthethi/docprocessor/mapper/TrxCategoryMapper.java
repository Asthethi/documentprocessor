package com.asthethi.docprocessor.mapper;

import com.asthethi.docprocessor.model.CategoryKeyword;
import com.asthethi.docprocessor.model.TrxCategoryRequest;
import com.asthethi.docprocessor.model.entity.TransactionCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrxCategoryMapper {

    public TransactionCategory toTransactionCategory(TrxCategoryRequest trxCategoryRequest) {

        TransactionCategory category = TransactionCategory.builder().name(trxCategoryRequest.getName()).build();

        List<CategoryKeyword> categoryKeywordList = trxCategoryRequest.getKeywords();

        if (categoryKeywordList != null) {
            categoryKeywordList.forEach(keyword -> keyword.setCategory(category));
            category.setKeywords(categoryKeywordList);
        }

        return category;
    }
}
