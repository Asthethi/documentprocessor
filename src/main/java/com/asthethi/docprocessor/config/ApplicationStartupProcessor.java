package com.asthethi.docprocessor.config;

import com.asthethi.docprocessor.model.CategoryKeyword;
import com.asthethi.docprocessor.model.TrxCategoryRequest;
import com.asthethi.docprocessor.model.entity.TransactionCategory;
import com.asthethi.docprocessor.service.TransactionCategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
public class ApplicationStartupProcessor implements CommandLineRunner {

    private TransactionCategoryService transactionCategoryService;

    @Override
    public void run(String... args) throws Exception {
        log.info("ApplicationStartupProcessor : saving transaction categories if does not exists");
        saveTransactionCategoriesIfNotExists();
    }

    private void saveTransactionCategoriesIfNotExists() {

        if (this.transactionCategoryService.findAllCategories().isEmpty()) {
            log.info("ApplicationStartupProcessor : trx categories not present in db, hence saving it from application.properteis");


            Map<String, List<String>> trxCategories = this.getPredefinedTransactionCategoriesFromProperties();

            saveTransactionCategories(trxCategories);
        } else {
            log.info("ApplicationStartupProcessor : trx categories already present in db");
            log.info("ApplicationStartupProcessor : checking for any new category added in app.properties!");

            addMissingCategoriesFromProperties();
        }

    }

    private void saveTransactionCategories(Map<String, List<String>> trxCategories) {
        trxCategories.forEach((categoryName, keyWordList) -> {
            List<CategoryKeyword> categoryKeywordList = new ArrayList<>();
            keyWordList.forEach(keyword -> categoryKeywordList.add(CategoryKeyword.builder().keyword(keyword).build()));

            this.transactionCategoryService.saveTransactionCategory(TrxCategoryRequest.builder().name(categoryName)
                    .keywords(categoryKeywordList).build());
        });
    }

    private Map<String, List<String>> getPredefinedTransactionCategoriesFromProperties() {
        Map<String, List<String>> trxCategoryKeywords = new HashMap<>();
        Properties properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {

            if(input == null) {
                throw new RuntimeException("application.properties not found in classpath");
            }
            properties.load(input);

            for (String key : properties.stringPropertyNames()) {
                if (key.startsWith("category")) {
                    String value = properties.getProperty(key);
                    List<String> keywords = Arrays.stream(value.split(",")).
                            map(s -> s.trim().toLowerCase())
                            .toList();

                    trxCategoryKeywords.put(key, keywords);
                }
            }

        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        return trxCategoryKeywords;
    }

    private void addMissingCategoriesFromProperties() {
        Map<String, List<String>> trxCategories = this.getPredefinedTransactionCategoriesFromProperties();
        List<TransactionCategory> transactionCategories = this.transactionCategoryService.findAllCategories();

        // Convert DB categories to Set for fast lookup
        Set<String> dbCategories = new HashSet<>();
        for (TransactionCategory trxCategory : transactionCategories) {
            dbCategories.add(trxCategory.getName());
        }

        // add missing category (if app.properties has any newly added category add it to the db)
        for(String key : trxCategories.keySet()) {
            if(!dbCategories.contains(key)) {
                log.info("ApplicationStartupProcessor : new category has been found -> {}", key);
                log.info("ApplicationStartupProcessor : attempting to save new category");
                saveTransactionCategories(Map.of(key,trxCategories.get(key)));
            }
        }

        // remove category from db (if app.properties has removed any category , remove it from db)
        for(String dbCategory : dbCategories) {
            if(!trxCategories.containsKey(dbCategory)) {
                log.info("ApplicationStartupProcessor : a category has been removed from application.properties -> {}", dbCategory);
                log.info("ApplicationStartupProcessor : attempting to remove the category from db");
                transactionCategoryService.deleteTransactionCategoryByName(dbCategory);
            }
        }

    }
}
