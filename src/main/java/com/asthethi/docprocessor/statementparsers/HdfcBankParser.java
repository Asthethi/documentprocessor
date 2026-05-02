package com.asthethi.docprocessor.statementparsers;

import com.asthethi.docprocessor.constants.ApplicationConstants;
import com.asthethi.docprocessor.model.FileRequest;
import com.asthethi.docprocessor.model.FileType;
import com.asthethi.docprocessor.model.TransactionResponse;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class HdfcBankParser implements BankParser {

    Map<String, List<String>> trxCategoryKeywords = new HashMap<>();

    @Value("${bank.statement-expense-categories}")
    private String[] allowedCategoryList;

    private Map<FileType, StatementFileParser> parsers;

    private List<TransactionResponse> transactions;

    private HdfcTxtStatementParser hdfcTxtStatementParser;

    private HdfcPdfStatementParser hdfcPdfStatementParser;

    @PostConstruct
    public void init(){
        this.parsers = Map.of(
                FileType.TEXT, hdfcTxtStatementParser,
                FileType.PDF, hdfcPdfStatementParser
        );
    }

    @Override
    public List<TransactionResponse> parseStatement(FileRequest file) throws IOException {
        log.info("File Type : {}", file.getFileType());
        log.info("File Name : {}", file.getDocument().getOriginalFilename());
        StatementFileParser parser = this.parsers.get(file.getFileType()); // TODO: Add a filetype check for txt and pdf, if somthing else, please throw an exception UnsupportedDocumentException
        this.transactions = parser.parse(file);
        return parser.parse(file);
    }


    @Override
    public List<TransactionResponse> getSpecificCategoryTransactions(String transactionCategory, MultipartFile document) throws IOException {
        return this.transactions.stream().
                filter(transaction -> transaction.getNarration().contains(transactionCategory.toUpperCase())).
                collect(Collectors.toList());
    }

    @Override
    public Map<String, Double> getCategoryWiseTotalExpense(MultipartFile document) throws IOException {
        HashMap<String, Double> categoryWiseTotalExpense = new HashMap<>();
        Arrays.asList(allowedCategoryList).forEach(category -> categoryWiseTotalExpense.
                put(category, getTotalExpense(category, this.transactions)));
        return categoryWiseTotalExpense;
    }

    private double getTotalExpense(String category, List<TransactionResponse> allTransactions) {
        double totalExpense = 0;
        for (TransactionResponse transaction : allTransactions) {

            // Convert category to uppercase once to avoid repetition
            String categoryUpper = category.toUpperCase();

            // Check for ACH category condition and the common case for other categories
            if (transaction.getNarration().contains(categoryUpper)) {
                if (categoryUpper.equals(ApplicationConstants.TRANSACTION_CATEGORY_ACH) && transaction.getNarration().contains(ApplicationConstants.HDFC_BANK_STRING)) {
                    totalExpense += transaction.getDebitAmount();
                } else if (!categoryUpper.equals(ApplicationConstants.TRANSACTION_CATEGORY_ACH)) {
                    totalExpense += transaction.getDebitAmount();
                }
            }
        }
        return Double.parseDouble(String.format("%.2f", totalExpense));
    }

    @Override
    public List<String> getAllExpenseCategories() {
        return Arrays.asList(allowedCategoryList);
    }

    @Override
    public LinkedHashMap<String, Map<String, Object>> getMonthWiseExpenseReport(MultipartFile document) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        LinkedHashMap<String, Map<String, Object>> monthWiseReport = new LinkedHashMap<>();

        for (TransactionResponse transaction : this.transactions) {
            LocalDate date = LocalDate.parse(transaction.getTransactionDate(), formatter);
            String transactionMonth = date.getMonth().name();

            double debitAmount = 0.0;
            if(Objects.nonNull(transaction.getDebitAmount())) {
                debitAmount = transaction.getDebitAmount();
            }

            if (debitAmount > 0) {

                monthWiseReport.putIfAbsent(transactionMonth, new LinkedHashMap<>());
                Map<String, Object> monthData = monthWiseReport.get(transactionMonth);

                // Update total expense for the month
                monthData.put("expense", roundToTwoDecimals(
                        (double) monthData.getOrDefault("expense", 0.0) + debitAmount
                ));

                // Update category-wise expense
                Map<String, Double> categoryWiseExpense = (Map<String, Double>) monthData.getOrDefault("details", new LinkedHashMap<>());
                categoryWiseExpense.merge(transaction.getTransactionCategory(), debitAmount, (oldValue, newValue) -> roundToTwoDecimals(oldValue + newValue));

                monthData.put("details", categoryWiseExpense);
            }
        }

        return monthWiseReport;
    }

    public static double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP) // Rounds to 2 decimal places
                .doubleValue();
    }

    @Override
    public List<TransactionResponse> getTransactionByMonth(MultipartFile document, String month){

        return null;
    }

    @Override
    public String getBankParserName() {
        return "HDFC";
    }

}
