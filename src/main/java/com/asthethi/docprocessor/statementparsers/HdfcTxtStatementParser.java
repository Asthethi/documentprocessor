package com.asthethi.docprocessor.statementparsers;

import com.asthethi.docprocessor.constants.ApplicationConstants;
import com.asthethi.docprocessor.mapper.TransactionMapper;
import com.asthethi.docprocessor.model.FileRequest;
import com.asthethi.docprocessor.model.Transaction;
import com.asthethi.docprocessor.model.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class HdfcTxtStatementParser implements StatementFileParser {

    @Value("${bank.statement-expense-categories}")
    private String[] allowedCategoryList;

    @Autowired
    private TransactionMapper transactionMapper;

    @Override
    public List<TransactionResponse> parse(FileRequest file) throws IOException {
        return getAllTransactionsFromTxtStatement(file.getDocument());
    }

    private List<TransactionResponse> getAllTransactionsFromTxtStatement(MultipartFile txtFile) throws IOException {
        InputStream inputStream = txtFile.getInputStream();
        String line;
        List<TransactionResponse> allTransactions = new ArrayList<>();

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

                if (Objects.isNull(transactionCategory)) {
                    transactionCategory = "Misc";
                }

                allTransactions.add(new TransactionResponse(transaction[0].trim(), transaction[1].trim(),
                        Double.valueOf(transaction[3]), Double.valueOf(transaction[4]), transaction[5].trim(), transaction[6],
                        null, transactionCategory));

            }
        }

        //return allTransactions.stream().map(transactionMapper::toTransactionResponse).toList();
        return allTransactions;
    }



}
