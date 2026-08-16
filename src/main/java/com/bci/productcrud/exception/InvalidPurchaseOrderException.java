package com.bci.productcrud.exception;

public class InvalidPurchaseOrderException extends RuntimeException {
    public InvalidPurchaseOrderException(String message) {
        super(message);
    }
}
