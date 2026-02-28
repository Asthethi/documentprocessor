package com.asthethi.docprocessor.factory;
import com.asthethi.docprocessor.statementparsers.BankParser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.asthethi.docprocessor.constants.ApplicationConstants.HDFC_BANK;

@Component
public class BankParserFactory {

    private Map<String, BankParser> parsers;

    public BankParserFactory(List<BankParser> bankParserList) {
        this.parsers = bankParserList.stream().
                collect(Collectors.
                        toMap(BankParser::getBankParserName, Function.identity()));
    }

    public BankParser getBankParser(String bankName) {
        return this.parsers.get(bankName);
    }
}
