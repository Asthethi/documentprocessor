package com.asthethi.docprocessor.reporitory;

import com.asthethi.docprocessor.model.entity.BankStatement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatementRepository extends JpaRepository<BankStatement, Long> {
}
