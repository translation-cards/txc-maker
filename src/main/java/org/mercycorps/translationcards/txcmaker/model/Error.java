package org.mercycorps.translationcards.txcmaker.model;

public class Error {
    public String key;
    public String message;
    boolean fatal;

    public Error() {
        key = message = "";
        fatal = false;
    }

    public Error(String key, String message, boolean fatal) {
        this.key = key;
        this.message = message;
        this.fatal = fatal;
    }
}
