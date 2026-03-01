package com.asthethi.docprocessor.statementparsers;

import com.asthethi.docprocessor.model.FileRequest;
import com.asthethi.docprocessor.model.TransactionResponse;

import java.io.IOException;
import java.util.List;

public interface StatementFileParser {
    List<TransactionResponse> parse(FileRequest file) throws IOException;
}
