package com.transaction.order;

public class NotEnoughMoneyException extends Exception {
    public NotEnoughMoneyException(String message) {
        super(message);
    }
}

