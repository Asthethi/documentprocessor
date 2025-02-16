package com.asthethi.docprocessor.service;

import com.asthethi.docprocessor.constants.ApplicationConstants;
import com.asthethi.docprocessor.exception.UnsupportedDocumentException;
import com.asthethi.docprocessor.model.FileRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.tags.EditorAwareTag;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Slf4j
public class PdfProcessorService {

    private static final Pattern DATE_PATTERN = Pattern.compile("\\b\\d{2}/\\d{2}/\\d{2}\\b");

    public List<String> getAllPdfText(FileRequest fileRequest) throws IOException {
        log.info("File Type : {}", fileRequest.getFileType());
        log.info("File Name : {}", fileRequest.getDocument().getOriginalFilename());
        switch (fileRequest.getFileType()) {
            case PDF -> {
                return getGroceryTransactions(fileRequest.getDocument());
            }
            case TEXT -> {
                throw new UnsupportedDocumentException(ApplicationConstants.UNSUPPORTED_DOCUMENT_TYPE_ERR);
            }
            default -> throw new UnsupportedDocumentException(ApplicationConstants.UNSUPPORTED_DOCUMENT_TYPE_ERR);
        }
    }

    private String getPdfContent(MultipartFile document) throws IOException {
        try (InputStream inputStream = document.getInputStream();
             PDDocument doc = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String pdfContent = stripper.getText(doc);
            String[] lines = pdfContent.split("\\r?\\n");
            StringBuilder groceryTransactions = new StringBuilder();

            for (String line : lines) {
                Matcher matcher = DATE_PATTERN.matcher(line);
                int dateCount = 0;
                while (matcher.find()) {
                    dateCount++;
                }


                groceryTransactions.append(line).append("\n");
            }

            return null;
//            StringBuilder groceryTransactions = new StringBuilder();
//
//            for (String line : lines) {
//                if (line.contains("GROCERY")) {
//                    groceryTransactions.append(line).append("\n");
//                }
//            }
//
//            return groceryTransactions.toString().trim();
        }
    }

    public static List<String> extractGroceryTransactions(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String pdfContent = stripper.getText(document);

            List<String> transactions = new ArrayList<>();
            StringBuilder currentTransaction = new StringBuilder();
            boolean isNewTransaction = true;

            for (String line : pdfContent.split("\\r?\\n")) {
                Matcher matcher = DATE_PATTERN.matcher(line);
                int dateCount = 0;

                // Count occurrences of the date in the current line
                while (matcher.find()) {
                    dateCount++;
                }

                if (dateCount > 0 && isNewTransaction) {
                    // If a new transaction starts, save the previous one
                    if (currentTransaction.length() > 0) {
                        transactions.add(currentTransaction.toString().trim());
                    }
                    currentTransaction.setLength(0); // Reset for new transaction
                    isNewTransaction = false; // Prevent false splits
                }

                // Append line to the transaction
                currentTransaction.append(line).append(" ");

                // If we encounter a third occurrence of a date, treat it as a new transaction
                if (dateCount == 2) {
                    isNewTransaction = true;
                }
            }

            // Add the last transaction if any
            if (currentTransaction.length() > 0) {
                transactions.add(currentTransaction.toString().trim());
            }

            // Filter transactions containing "GROCERY"
            List<String> groceryTransactions = new ArrayList<>();
            for (String transaction : transactions) {
                if (transaction.toUpperCase().contains("GROCERY")) {
                    groceryTransactions.add(transaction);
                }
            }

            return groceryTransactions;
        }
    }

    public static List<String> getGroceryTransactions(MultipartFile documentFile) throws IOException {
        List<String> transactions;
        List<String> groceryTransactions;
        try (InputStream inputStream = documentFile.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String pdfContent = stripper.getText(document);
            String pdfLines[] = pdfContent.split("\\r?\\n");

            transactions = new ArrayList<>();
            StringBuilder currentTransaction = new StringBuilder();
            boolean newTransaction = false;

            for (String line : pdfLines) {
                Matcher matcher = DATE_PATTERN.matcher(line);
                int dateCount = 0;
                while (matcher.find()) {
                    dateCount++;
                }

                newTransaction = dateCount == 2 ? true : false;

                if (dateCount == 2 && currentTransaction.length() > 0 && DATE_PATTERN.matcher(currentTransaction).matches()) {

                    if(transactions.size() > 0){
                        transactions.add(transactions.size() - 1, transactions.get(transactions.size() - 1) + currentTransaction.toString().trim());
                    }else{
                        transactions.add(currentTransaction.toString().trim());
                    }


                    currentTransaction.setLength(0);
                }

                if (dateCount == 2 && newTransaction) {
                    currentTransaction.append(line).append("\n");
                    transactions.add(currentTransaction.toString().trim());
                    currentTransaction.setLength(0);
                    continue;
                }

                if (dateCount == 0) {
                    currentTransaction.append(line).append(" ");
                }

            }

            groceryTransactions = new ArrayList<>();
            for (String transaction : transactions) {
                if (transaction.toUpperCase().contains("GROCERY")) {
                    groceryTransactions.add(transaction);
                }
            }
        }

        return groceryTransactions;
    }
}
