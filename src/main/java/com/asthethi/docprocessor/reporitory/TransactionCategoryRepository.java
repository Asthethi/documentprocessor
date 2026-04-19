package com.asthethi.docprocessor.reporitory;

import com.asthethi.docprocessor.model.entity.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {
    @Query("SELECT tc FROM TransactionCategory tc LEFT JOIN FETCH tc.keywords")
    List<TransactionCategory> findAllWithKeywords();

    void deleteByName(String name);
}
