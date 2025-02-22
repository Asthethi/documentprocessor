package com.asthethi.docprocessor.controller;

import com.asthethi.docprocessor.model.FileRequest;
import com.asthethi.docprocessor.service.PdfProcessorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("pdf/statement")
public class BankStatementPdfController {

    private PdfProcessorService pdfProcessorService;

    @GetMapping("/gettext")
    public ResponseEntity<List<Transaction>> getAllTransactionsFromFile(@ModelAttribute FileRequest fileRequest) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).
                body(pdfProcessorService.getAllPdfText(fileRequest));
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint(){
        return ResponseEntity.status(HttpStatus.OK).
                body("Test Success");
    }
}
