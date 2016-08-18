package org.mercycorps.translationcards.txcmaker.model;

public class DeckMetadata {

    public String documentId;
    public String directoryId;

    public DeckMetadata(String documentId, String directoryId) {
        this.documentId = documentId;
        this.directoryId = directoryId;
    }
}
