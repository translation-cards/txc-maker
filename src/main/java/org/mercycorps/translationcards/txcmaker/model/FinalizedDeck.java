package org.mercycorps.translationcards.txcmaker.model;

import java.util.ArrayList;
import java.util.List;

public class FinalizedDeck {

    public String license_url;
    public String readme;
    public boolean locked;
    public long timestamp;
    public String deck_label;
    public String publisher;
    public String id;
    public String source_language;
    public List<FinalizedLanguage> languages;

    public FinalizedDeck() {
        languages = new ArrayList<>();
    }

    public FinalizedDeck setLicense_url(String license_url) {
        this.license_url = license_url;
        return this;
    }

    public FinalizedDeck setReadme(String readme) {
        this.readme = readme;
        return this;
    }

    public FinalizedDeck setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public FinalizedDeck setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public FinalizedDeck setDeck_label(String deck_label) {
        this.deck_label = deck_label;
        return this;
    }

    public FinalizedDeck setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    public FinalizedDeck setId(String id) {
        this.id = id;
        return this;
    }

    public FinalizedDeck setSource_language(String source_language) {
        this.source_language = source_language;
        return this;
    }

    public FinalizedDeck setLanguages(List<FinalizedLanguage> languages) {
        this.languages = languages;
        return this;
    }
}
