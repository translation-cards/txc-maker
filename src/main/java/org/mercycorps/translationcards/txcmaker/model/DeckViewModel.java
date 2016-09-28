package org.mercycorps.translationcards.txcmaker.model;

import java.util.List;

public class DeckViewModel {
    private String deck_label;
    private String language_label;
    private String id;
    private List<Language> languages;
    private int numberOfErrors;

    public DeckViewModel(String deck_label, String language_label, String id, List<Language> languages, int numberOfErrors) {
        this.deck_label = deck_label;
        this.language_label = language_label;
        this.id = id;
        this.languages = languages;
        this.numberOfErrors = numberOfErrors;
    }
}
