package org.mercycorps.translationcards.txcmaker.model.deck;

import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.Language;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deck {

    public String deck_label;
    public String publisher;
    public String iso_code;
    public String language_label;
    public String id;
    public long timestamp;
    public String license_url;
    public boolean locked;
    public List<Language> languages;
    public List<Error> errors;
    public transient List<Error> parseErrors;
    private transient Map<String, Language> languageLookup;

    public Deck() {
        languages = new ArrayList<>();
        errors = new ArrayList<>();
        parseErrors = new ArrayList<>();
        languageLookup = new HashMap<>();
    }

    public Deck setDeckLabel(String deckLabel) {
        this.deck_label = deckLabel;
        return this;
    }

    public Deck setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    public Deck setLanguage(String iso_code) {
        this.iso_code = iso_code;
        return this;
    }

    public Deck setLanguageLabel(String language_label) {
        this.language_label = language_label;
        return this;
    }

    public Deck setDeckId(String deckId) {
        this.id = deckId;
        return this;
    }

    public Deck setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Deck setLicenseUrl(String licenseUrl) {
        this.license_url = licenseUrl;
        return this;
    }

    public Deck setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public Deck addCard(String iso_code, String language_label, Card card) {
        if (!languageLookup.containsKey(language_label)) {
            Language langSpec = new Language(iso_code, language_label);
            languages.add(langSpec);
            languageLookup.put(language_label, langSpec);
        }
        languageLookup.get(language_label).addCard(card);
        return this;
    }

    public static Deck initializeDeckWithFormData(HttpServletRequest req) {
        return new Deck()
                .setDeckLabel(req.getParameter("deckName"))
                .setPublisher(req.getParameter("publisher"))
                .setDeckId(req.getParameter("deckId"))
                .setTimestamp(System.currentTimeMillis())
                .setLicenseUrl(req.getParameter("licenseUrl"))
                .setLanguage("en")
                .setLanguageLabel("English");
    }


    public int getNumberOfErrors() {
        return errors.size();
    }

}
