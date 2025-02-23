package com.asthethi.docprocessor.service;

import com.asthethi.docprocessor.constants.ApplicationConstants;
import com.asthethi.docprocessor.exception.UnsupportedDocumentException;
import com.asthethi.docprocessor.model.FileRequest;
import com.asthethi.docprocessor.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PdfProcessorService {

    private static final Pattern DATE_PATTERN = Pattern.compile("\\b\\d{2}/\\d{2}/\\d{2}\\b");

    public List<Transaction> getAllPdfText(FileRequest fileRequest) throws IOException {
        log.info("File Type : {}", fileRequest.getFileType());
        log.info("File Name : {}", fileRequest.getDocument().getOriginalFilename());
        switch (fileRequest.getFileType()) {
            case PDF -> {
                throw new UnsupportedDocumentException("PDF file is not supported");
            }
            case TEXT -> {
                return getAllTransactionsFromTxt(fileRequest.getDocument());
            }
            default -> throw new UnsupportedDocumentException(ApplicationConstants.UNSUPPORTED_DOCUMENT_TYPE_ERR);
        }
    }

    public static List<String> getAllTransactionsFromPdf(MultipartFile documentFile) throws IOException {
        StringBuilder aTransaction  = new StringBuilder();
        List<String> transactions = new ArrayList<>();

        try (InputStream inputStream = documentFile.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper stripper = new PDFTextStripper();
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

    private static List<Transaction> getAllTransactionsFromTxt(MultipartFile txtFile) throws IOException {

        InputStream inputStream = txtFile.getInputStream();

        String line;
        List<Transaction> allTransactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            br.readLine();
            String headers = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] transaction = line.trim().split(",");

                allTransactions.add(new Transaction(transaction[0].trim(),
                        transaction[1].trim(),
                        Double.valueOf(transaction[3]),
                        Double.valueOf(transaction[4]),
                        transaction[5].trim(),
                        transaction[6],
                        null));

            }
        }

        return allTransactions;
    }

    public List<Transaction> getSpecificCategoryTransactions(String transactionCategory, MultipartFile document) throws IOException {
        List<Transaction> allTransactions = getAllTransactionsFromTxt(document);
        return allTransactions.stream().
                filter(transaction -> transaction.getNarration().contains(transactionCategory.toUpperCase())).
                collect(Collectors.toList());
    }

    public HashMap<String, Double> getCategoryWiseTotalExpense(MultipartFile document) throws IOException {

        if(Objects.isNull(document)){
            //TODO: Log Exception here
        }

        HashMap<String, Double> categoryWiseTotalExpense = new HashMap<>();
        String[] categories = {"grocery", "sride", "petrol", "ACH", "BILL"};
        List<Transaction> allTransactions = getAllTransactionsFromTxt(document);
        Arrays.asList(categories).forEach(category -> {
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
}
