package org.mercycorps.translationcards.txcmaker.model;

public class ImportDeckForm {

    private String docId;
    private String audioDirId;
    private String deckName;
    private String publisher;
    private String licenseUrl;
    private boolean locked;
    private String deckId;

    public String getDocId() {
        return docId;
    }

    public ImportDeckForm setDocId(String docId) {
        this.docId = docId;
        return this;
    }

    public String getAudioDirId() {
        return audioDirId;
    }

    public ImportDeckForm setAudioDirId(String audioDirId) {
        this.audioDirId = audioDirId;
        return this;
    }

    public String getDeckName() {
        return deckName;
    }

    public ImportDeckForm setDeckName(String deckName) {
        this.deckName = deckName;
        return this;
    }

    public String getPublisher() {
        return publisher;
    }

    public ImportDeckForm setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public ImportDeckForm setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public ImportDeckForm setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public String getDeckId() {
        return deckId;
    }

    public ImportDeckForm setDeckId(String deckId) {
        this.deckId = deckId;
        return this;
    }
}
