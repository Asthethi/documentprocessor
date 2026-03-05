package com.asthethi.docprocessor.controller;

import com.asthethi.docprocessor.factory.BankParserFactory;
import com.asthethi.docprocessor.model.FileRequest;
import com.asthethi.docprocessor.model.TransactionCategoryRequest;
import com.asthethi.docprocessor.model.TransactionResponse;
import com.asthethi.docprocessor.model.entity.BankStatement;
import com.asthethi.docprocessor.service.BankStatementService;
import com.asthethi.docprocessor.statementparsers.BankParser;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("bank/account/statement")
public class StatementController {

    private BankParserFactory bankParserFactory;

    private BankStatementService bankStatementService;

    @PostMapping(value = "all/transactions/{bankName}", consumes = "multipart/form-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionResponse>> getTransactions(@ModelAttribute @Valid FileRequest fileRequest,
                                                                     @PathVariable("bankName") String bankName) throws IOException {
        BankParser statementParser = bankParserFactory.getBankParser(bankName);
        List<TransactionResponse> transactions = statementParser.parseStatement(fileRequest);
        return ResponseEntity.status(HttpStatus.OK).
                body(transactions);
    }

    @GetMapping("/transactions/category{bankName}")
    public ResponseEntity<List<TransactionResponse>> getSpecificCategoryTransactions(@ModelAttribute @Valid
                                                                             TransactionCategoryRequest transactionCategoryRequest,
                                                                             @PathVariable("bankName") String bankName) throws IOException {

        return ResponseEntity.status(HttpStatus.OK).body(bankParserFactory.getBankParser(bankName).
                getSpecificCategoryTransactions(transactionCategoryRequest.getCategory(),
                        transactionCategoryRequest.getDocument()));
    }

    @PostMapping(value = "/transactions/category/totalexpense/{bankName}", consumes = "multipart/form-data" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Double>> getCategoryWiseTotalExpense
            (@RequestParam MultipartFile document, @PathVariable("bankName") String bankName) throws IOException {
        HashMap<String, Double> expenses = bankParserFactory.getBankParser(bankName).getCategoryWiseTotalExpense(document);
        return ResponseEntity.status(HttpStatus.OK).body(expenses);
    }

    @GetMapping(value = "/expensecategories/{bankName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllExpenseCategories(@PathVariable("bankName") String bankName){
        return ResponseEntity.status(HttpStatus.OK).body(bankParserFactory.getBankParser(bankName).getAllExpenseCategories());
    }

    @PostMapping(value = "/transactions/monthwise/expense/{bankName}", consumes = "multipart/form-data" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashMap<String, Map<String, Object>>> getMonthWiseExpenseReport
            (@RequestParam MultipartFile document,
             @PathVariable("bankName")  String bankName) throws IOException {
        LinkedHashMap<String, Map<String, Object>> expenses = bankParserFactory.getBankParser(bankName).getMonthWiseExpenseReport(document);
        return ResponseEntity.status(HttpStatus.OK).body(expenses);
    }
    @PostMapping(value = "/transactions/bankstatement/save/{bankName}" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveBankStatement(@RequestBody List<TransactionResponse> request, @PathVariable String bankName){
        this.bankStatementService.saveStatement(request, bankName);
        List<BankStatement> response = this.bankStatementService.fetchAllTransactions();
        return null;
    }

    @PostMapping(value = "/transactions/save/{bankName}/{month}" , consumes = "multipart/form-data" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionResponse>> getSpecificMonthTransactions(@RequestParam MultipartFile document,
                                                                                  @PathParam("month") String month,
                                                                                  @PathVariable("bankName") String bankName){
        return ResponseEntity.status(HttpStatus.OK).body(bankParserFactory.getBankParser(bankName).getTransactionByMonth(document , month));
    }
}
