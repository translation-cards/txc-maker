package org.mercycorps.translationcards.txcmaker.model;

public class Error {
    public String key;
    public String message;

    public Error(String key, String message) {
        this.key = key;
        this.message = message;
    }
}
