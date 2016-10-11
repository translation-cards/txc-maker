package org.mercycorps.translationcards.txcmaker.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public NewDeck(String sourceLanguage, String deckLabel, String author, long timestamp, boolean locked, String license_url, String readme, List<NewCard> cards) {
        this.sourceLanguage = sourceLanguage;
        this.deckLabel = deckLabel;
        this.author = author;
        this.timestamp = timestamp;
        this.locked = locked;
        this.license_url = license_url;
        this.readme = readme;
        this.translations = buildTranslationsFromCards(cards);
        this.destinationLanguageNames = buildDestinationLanguageNames(cards);
    }

    List<String> buildDestinationLanguageNames(List<NewCard> cards) {
        Map<String, Object> destinationLanguageNames = new HashMap<>();
        for (NewCard card : cards) {
            destinationLanguageNames.put(card.getDestinationLanguageName(), null);
        }
        return new ArrayList<>(destinationLanguageNames.keySet());
    }

    List<Translation> buildTranslationsFromCards(List<NewCard> cards) {
        Map<String, Translation> sourcePhraseToTranslation = new HashMap<>();
        for (NewCard card : cards) {
            String sourcePhrase = card.getSourcePhrase();
            if (sourcePhraseToTranslation.containsKey(sourcePhrase)) {
                sourcePhraseToTranslation.get(sourcePhrase).addCard(card);
            } else {
                sourcePhraseToTranslation.put(sourcePhrase, createTranslationFromCard(card));
            }
        }
        return new ArrayList<>(sourcePhraseToTranslation.values());
    }

    private Translation createTranslationFromCard(NewCard card) {
        List<NewCard> cardListForTranslation = new ArrayList<>();
        cardListForTranslation.add(card);
        return new Translation(cardListForTranslation);
    }
}
