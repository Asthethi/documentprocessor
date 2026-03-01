package com.asthethi.docprocessor.service;

import com.asthethi.docprocessor.constants.ApplicationConstants;
import com.asthethi.docprocessor.exception.UnsupportedDocumentException;
import com.asthethi.docprocessor.mapper.TransactionMapper;
import com.asthethi.docprocessor.model.FileRequest;
import com.asthethi.docprocessor.model.Transaction;
import com.asthethi.docprocessor.model.TransactionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BankStatementProcessorService {

    private TransactionMapper transactionMapper;

    @Value("${bank.statement-expense-categories}")
    private String[] allowedCategoryList;

    private static final Pattern DATE_PATTERN = Pattern.compile("\\b\\d{2}/\\d{2}/\\d{2}\\b");
    private static final Pattern BALANCE_PATTERN =
            Pattern.compile("\\d{1,3}(,\\d{3})*\\.\\d{2}$");

//    private static final Pattern TRANSACTION_PATTERN =
//            Pattern.compile("^(\\d{2}/\\d{2}/\\d{2})\\s+(.*?)\\s+(\\S+)\\s+(\\d{2}/\\d{2}/\\d{2})\\s+([\\d,]+\\.\\d{2})\\s+([\\d,]+\\.\\d{2})\\s*$");

    private static final Pattern TRANSACTION_PATTERN = Pattern.compile("^(\\d{2}/\\d{2}/\\d{2})\\s+(.*?)\\s+(\\S+)\\s+(\\d{2}/\\d{2}/\\d{2})\\s+([\\d,]+\\.\\d{2})\\s+([\\d,]+\\.\\d{2})\\s*$");

    public List<TransactionResponse> getTransactions(FileRequest fileRequest) throws IOException {
        log.info("File Type : {}", fileRequest.getFileType());
        log.info("File Name : {}", fileRequest.getDocument().getOriginalFilename());
        switch (fileRequest.getFileType()) {
            case PDF -> {
                List<TransactionResponse> pdfTrxs = getTransactionsFromPdfFile(fileRequest.getDocument());
                return pdfTrxs;
            }
            case TEXT -> {
                return getAllTransactionsFromTxtStatement(fileRequest.getDocument())
                        .stream().map(transactionMapper :: toTransactionResponse).toList();
            }
            default -> throw new UnsupportedDocumentException(ApplicationConstants.UNSUPPORTED_DOCUMENT_TYPE_ERR);
        }
    }

    public static List<String> getAllTransactionsFromPdf(MultipartFile documentFile) throws IOException {
        StringBuilder aTransaction  = new StringBuilder();
        List<String> transactions = new ArrayList<>();

        try (InputStream inputStream = documentFile.getInputStream();
             PDDocument document = PDDocument.load(inputStream, "71023607")) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String pdfContent = stripper.getText(document);
            String pdfLines[] = pdfContent.split("\\r?\\n");

            for (String line : pdfLines) {
                int dateCount = 0;
                Matcher matcher = DATE_PATTERN.matcher(line);

                // find the number of dates in a line
                while (matcher.find()) {
                    dateCount++;
                }

                // if line has 2 dates then its a new transaction
                if (dateCount == 2) {
                    if(aTransaction.toString().trim() == "") {
                        aTransaction.append(line).append("\n");
                    }else{
                        transactions.add(aTransaction.toString().trim());
                        aTransaction.setLength(0);
                        aTransaction.append(line).append("\n");
                    }
                }

                if(dateCount == 0 && aTransaction.length() > 0) {
                    aTransaction.append(line);
                }
            }

        }

        return transactions;
    }

    private List<Transaction> getAllTransactionsFromTxtStatement(MultipartFile txtFile) throws IOException {

        InputStream inputStream = txtFile.getInputStream();

        String line;
        List<Transaction> allTransactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            br.readLine();
            String headers = br.readLine();

            while ((line = br.readLine()) != null) {

                String[] transaction = line.trim().split(",");
                String narration = transaction[1].trim();
                String transactionCategory = null;

                for (String category : allowedCategoryList) {

                    if (category.equals(ApplicationConstants.TRANSACTION_CATEGORY_ACH)) {
                        if (narration.toUpperCase().contains(ApplicationConstants.TRANSACTION_CATEGORY_ACH) && narration.toUpperCase().contains(ApplicationConstants.HDFC_BANK_STRING)) {
                            transactionCategory = category;
                        }
                    } else {
                        if (narration.toUpperCase().contains(category)) {
                            transactionCategory = category;
                        }
                    }
                }

                if(Objects.isNull(transactionCategory)){
                    transactionCategory = "Misc";
                }

                allTransactions.add(new Transaction(transaction[0].trim(), transaction[1].trim(),
                        Double.valueOf(transaction[3]), Double.valueOf(transaction[4]), transaction[5].trim(), transaction[6],
                        null, transactionCategory));

            }
        }


        return allTransactions;
    }

    public List<Transaction> getSpecificCategoryTransactions(String transactionCategory, MultipartFile document) throws IOException {
        List<Transaction> allTransactions = getAllTransactionsFromTxtStatement(document);
        return allTransactions.stream().
                filter(transaction -> transaction.getNarration().contains(transactionCategory.toUpperCase())).
                collect(Collectors.toList());
    }

    public HashMap<String, Double> getCategoryWiseTotalExpense(MultipartFile document) throws IOException {
        HashMap<String, Double> categoryWiseTotalExpense = new HashMap<>();
        List<Transaction> allTransactions = getAllTransactionsFromTxtStatement(document);
        Arrays.asList(allowedCategoryList).forEach(category -> {
            categoryWiseTotalExpense.
                    put(category, getTotalExpense(category, allTransactions));
        });
        return categoryWiseTotalExpense;
    }

    private double getTotalExpense(String category, List<Transaction> allTransactions) {
        double totalExpense = 0;
        for (Transaction transaction : allTransactions) {

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

    public List<String> getAllExpenseCategories() {
        return Arrays.asList(allowedCategoryList);
    }

    public LinkedHashMap<String, Map<String, Object>> getMonthWiseExpenseReport(MultipartFile document) throws IOException {
        List<Transaction> allTransactions = getAllTransactionsFromTxtStatement(document);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        LinkedHashMap<String, Map<String, Object>> monthWiseReport = new LinkedHashMap<>();

        for (Transaction transaction : allTransactions) {
            LocalDate date = LocalDate.parse(transaction.getTransactionDate(), formatter);
            String transactionMonth = date.getMonth().name();
            double debitAmount = transaction.getDebitAmount();

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

    public List<TransactionResponse> getTransactionByMonth(MultipartFile document, String month){

        return null;
    }

    private static List<TransactionResponse> getTransactionsFromPdfFile(MultipartFile inputFile) throws IOException {

        TransactionResponse currentTrx = null;
        List<TransactionResponse> transactionResponseList = new ArrayList<>();

        try (InputStream inputStream = inputFile.getInputStream();
                PDDocument document = PDDocument.load(inputStream, "71023607")) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String pdfContent = stripper.getText(document);
            String pdfLines[] = pdfContent.split("\\r?\\n");

            for (String line : pdfLines) {
                    if (isTransactionRow(line)) {

                        if(currentTrx!=null) {
                            transactionResponseList.add(currentTrx);
                        }

                        currentTrx = extractTransaction(line);

                    } else if(currentTrx!=null && isTrxNarration(line)) {
                        currentTrx.setNarration(currentTrx.getNarration() + " " + line.trim());
                    }
            }

                            if (currentTrx != null) {
                    transactionResponseList.add(currentTrx);
                }

        }

        transactionResponseList.forEach(t-> {
            System.out.println(t.toString());
        });
        System.out.println(transactionResponseList.size());

        return transactionResponseList;
    }

    private static boolean isTrxNarration(String line) {
        return !line.contains("Closing Balance") &&
                !line.contains("End of Statement") &&
                !line.contains("Opening Balance") &&
                !line.contains("Dr Count") &&
                !line.contains("Generated On") &&
                !line.contains("STATEMENT SUMMARY") &&
                !line.contains("Generated By") &&
                !line.contains("computer generated statement") &&
                !line.contains("signature") &&
                !line.contains("*Closing balance") &&
                !line.contains("Contents of this statement will be") &&
                !line.contains("this statement") &&
                !line.contains("HDFC BANK LIMITED") &&
                !line.contains("GSTIN number") &&
                !line.contains("Registered Office") &&
                !line.contains("State account");
    }

    private static TransactionResponse extractTransaction(String line) {

        Matcher matcher = TRANSACTION_PATTERN.matcher(line.trim());

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid transaction row: " + line);
        }

        return TransactionResponse.builder()
                .transactionDate(matcher.group(1))
                .narration(matcher.group(2))
                .refNumber(matcher.group(3))
                .debitAmount(
                        matcher.group(5) != null
                                ? Double.valueOf(matcher.group(5).replace(",", ""))
                                : null
                )
                .creditAmount(
                        matcher.group(5) != null
                                ? Double.valueOf(matcher.group(6).replace(",", ""))
                                : null
                )
                .closingBalance(matcher.group(6))
                .build();
    }

    private static boolean isTransactionRow(String candidate) {
        Matcher dateMatcher = DATE_PATTERN.matcher(candidate);
        int dateCount = 0;

        while(dateMatcher.find()){
            dateCount++;
        }

        boolean endsWithBalance =
                BALANCE_PATTERN.matcher(candidate.trim()).find();

        return dateCount ==2 && endsWithBalance;
    }
}
