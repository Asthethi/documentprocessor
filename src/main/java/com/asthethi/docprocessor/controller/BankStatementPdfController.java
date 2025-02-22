package com.asthethi.docprocessor.controller;

import com.asthethi.docprocessor.model.FileRequest;
import com.asthethi.docprocessor.model.Transaction;
import com.asthethi.docprocessor.service.PdfProcessorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("account/statement")
public class BankStatementPdfController {

    private PdfProcessorService pdfProcessorService;

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllPdfText(@ModelAttribute FileRequest fileRequest) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).
                body(pdfProcessorService.getAllPdfText(fileRequest));
    }

    @GetMapping("/transactions/{transactionCategory}")
    public ResponseEntity<List<Transaction>> getSpecificCategoryTransactions(@PathVariable("transactionCategory") String transactionCategory,
                                                                             @RequestParam MultipartFile document) throws IOException {
        List<Transaction> transactions = pdfProcessorService.getSpecificCategoryTransactions(transactionCategory, document);
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }
}
