package com.asthethi.docprocessor.reporitory;

import com.asthethi.docprocessor.model.entity.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {
}
