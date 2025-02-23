package com.asthethi.docprocessor.controller;

import com.asthethi.docprocessor.model.FileRequest;
import com.asthethi.docprocessor.model.Transaction;
import com.asthethi.docprocessor.model.TransactionCategoryRequest;
import com.asthethi.docprocessor.service.PdfProcessorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("account/statement")
public class BankStatementPdfController {

    private PdfProcessorService pdfProcessorService;

    @GetMapping("/transactions/all")
    public ResponseEntity<List<Transaction>> getAllPdfText(@ModelAttribute @Valid FileRequest fileRequest) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).
                body(pdfProcessorService.getAllPdfText(fileRequest));
    }

    @GetMapping("/transactions/category")
    public ResponseEntity<List<Transaction>> getSpecificCategoryTransactions(@ModelAttribute @Valid
                          TransactionCategoryRequest transactionCategoryRequest) throws IOException {
        List<Transaction> transactions = pdfProcessorService.
                getSpecificCategoryTransactions(transactionCategoryRequest.getCategory(),
                        transactionCategoryRequest.getDocument());
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

    @GetMapping("/transactions/category/totalexpense")
    public ResponseEntity<HashMap<String, Double>> getCategoryWiseTotalExpense
            (@RequestParam MultipartFile document) throws IOException {
        HashMap<String, Double> expenses = pdfProcessorService.getCategoryWiseTotalExpense(document);
        return ResponseEntity.status(HttpStatus.OK).body(expenses);
    }
}
