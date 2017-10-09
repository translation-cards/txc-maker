package org.mercycorps.translationcards.txcmaker.model.deck;

import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.Translation;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private String sourceLanguage;
    private String deckLabel;
    private String publisher;
    private long timestamp;
    private boolean locked;
    private String licenseUrl;
    private String readme;
    private transient List<Error> parsingErrors;
    private List<Error> errors;
    private List<Translation> translations;
    private List<String> destinationLanguages;
    private String id;

    // Required by gson
    private Deck() {}

    public Deck(String sourceLanguage,
                String deckLabel,
                String publisher,
                long timestamp,
                boolean locked,
                String id,
                String licenseUrl,
                String readme,
                List<Error> parsingErrors,
                List<Translation> translations,
                List<String> destinationLanguages) {
        this.sourceLanguage = sourceLanguage;
        this.deckLabel = deckLabel;
        this.publisher = publisher;
        this.timestamp = timestamp;
        this.locked = locked;
        this.id = id;
        this.licenseUrl = licenseUrl;
        this.readme = readme;
        this.parsingErrors = parsingErrors;
        this.translations = translations;
        this.destinationLanguages = destinationLanguages;
        errors = new ArrayList<>();
    }

    public boolean isValid() {
        if (!parsingErrors.isEmpty()) {
            return false;
        }

        for (Translation translation : translations) {
            if (!translation.isValid()) {
                return false;
            }
        }
        return true;
    }

    public void setParsingErrors(List<Error> parsingErrors) {
        this.parsingErrors = parsingErrors;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public Translation getTranslationForSourcePhrase(String sourcePhrase) {
        for (Translation translation : translations) {
            if (translation.getSourcePhrase().equals(sourcePhrase)) {
                return translation;
            }
        }
        // TODO: throw?
        return null;
    }

    public List<Error> getParsingErrors() {
        return parsingErrors;
    }

    public String getDeckLabel() {
        return deckLabel;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public String getId() {
        return id;
    }

    public String getLicense() {
        return licenseUrl;
    }

    public boolean getLocked() {
        return locked;
    }

    public String getPublisher() {
        return publisher;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getIsoCode() {
        // TODO: this needs to be an iso code
        return sourceLanguage;
    }

    public List<String> getDestinationLanguages() {
        return destinationLanguages;
    }

    public int getNumberOfErrors() {
        return errors.size();
    }

    public List<Error> getErrors() {
        return errors;
    }
}
