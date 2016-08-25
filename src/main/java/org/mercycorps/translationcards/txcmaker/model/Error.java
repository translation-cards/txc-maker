package org.mercycorps.translationcards.txcmaker.model;

public class Error {
    public String message;
    boolean fatal;

    public Error() {
        message = "";
        fatal = false;
    }

    public Error(String message, boolean fatal) {
        this.message = message;
        this.fatal = fatal;
    }
}
