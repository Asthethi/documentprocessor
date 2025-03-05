package com.asthethi.docprocessor.controller;

import com.asthethi.docprocessor.model.FileRequest;
import com.asthethi.docprocessor.model.Transaction;
import com.asthethi.docprocessor.model.TransactionCategoryRequest;
import com.asthethi.docprocessor.model.TransactionResponse;
import com.asthethi.docprocessor.service.BankStatementProcessorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("account/statement")
public class BankStatementController {

    private BankStatementProcessorService bankStatementProcessorService;

    @PostMapping(value = "/transactions/all" , consumes = "multipart/form-data" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionResponse>> getTransactions(@ModelAttribute @Valid FileRequest fileRequest) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).
                body(bankStatementProcessorService.getTransactions(fileRequest));
    }

    @GetMapping("/transactions/category")
    public ResponseEntity<List<Transaction>> getSpecificCategoryTransactions(@ModelAttribute @Valid
                                                                             TransactionCategoryRequest transactionCategoryRequest) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(bankStatementProcessorService.
                getSpecificCategoryTransactions(transactionCategoryRequest.getCategory(),
                        transactionCategoryRequest.getDocument()));
    }

    @GetMapping("/transactions/category/totalexpense")
    public ResponseEntity<HashMap<String, Double>> getCategoryWiseTotalExpense
            (@RequestParam MultipartFile document) throws IOException {
        HashMap<String, Double> expenses = bankStatementProcessorService.getCategoryWiseTotalExpense(document);
        return ResponseEntity.status(HttpStatus.OK).body(expenses);
    }

    @GetMapping(value = "/expensecategories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllExpenseCategories(){
        return ResponseEntity.status(HttpStatus.OK).body(bankStatementProcessorService.getAllExpenseCategories());
    }
}
