package com.asthethi.docprocessor.statementparsers;

import com.asthethi.docprocessor.model.FileRequest;
import com.asthethi.docprocessor.model.TransactionResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface BankParser {
    public List<TransactionResponse> parseStatement(FileRequest file) throws IOException;
    public List<TransactionResponse> getSpecificCategoryTransactions(String transactionCategory, MultipartFile document) throws IOException;
    public HashMap<String, Double> getCategoryWiseTotalExpense(MultipartFile file) throws IOException;
    public List<String> getAllExpenseCategories();
    public LinkedHashMap<String, Map<String, Object>> getMonthWiseExpenseReport(MultipartFile file) throws IOException;
    public List<TransactionResponse> getTransactionByMonth(MultipartFile file, String month);
    public String getBankParserName();
}
