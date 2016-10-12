package org.mercycorps.translationcards.txcmaker.model;

import java.util.List;

public class NewDeck {
    private String sourceLanguage;
    private String deckLabel;
    private String author;
    private long timestamp;
    private boolean locked;
    private String license_url;
    private String readme;
    private List<Translation> translations;
    private List<String> destinationLanguageNames;

    public NewDeck(String sourceLanguage,
                   String deckLabel,
                   String author,
                   long timestamp,
                   boolean locked,
                   String license_url,
                   String readme,
                   List<Translation> translations,
                   List<String> destinationLanguageNames) {
        this.sourceLanguage = sourceLanguage;
        this.deckLabel = deckLabel;
        this.author = author;
        this.timestamp = timestamp;
        this.locked = locked;
        this.license_url = license_url;
        this.readme = readme;
        this.translations = translations;
        this.destinationLanguageNames = destinationLanguageNames;
    }

    public boolean isValid() {
        for (Translation translation : translations) {
            if (!translation.isValid()) {
                return false;
            }
        }
        return true;
    }
}
