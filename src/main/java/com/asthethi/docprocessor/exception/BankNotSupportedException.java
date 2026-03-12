package com.asthethi.docprocessor.exception;

public class BankNotSupportedException extends RuntimeException {
    public BankNotSupportedException(String msg) {
        super(msg);
    }
}
