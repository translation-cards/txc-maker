package org.mercycorps.translationcards.txcmaker.model;

import java.util.List;

public class Translation {
    private List<Card> cards;
    private String sourcePhrase;

    // Required by gson
    private Translation() {}

    public Translation(List<Card> cards) {
        this.cards = cards;
        sourcePhrase = cards.isEmpty() ? "" : cards.get(0).getSourcePhrase();
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public String getSourcePhrase() {
        return sourcePhrase;
    }

    public boolean isValid() {
        return atLeastOneCardHasAudio();
    }

    private boolean atLeastOneCardHasAudio() {
        for (Card card : cards) {
            // TODO: this will NPE if empty CSV fields are parsed into null strings
            if(!card.getDestinationLanguageName().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // TODO: this logic is repeated bewteen these two methods, perhaps replace arraylist with hashmap for O(1) lookup?
    public Card getCardForLanguage(String languageName) {
        for (Card card : cards) {
            if (card.getDestinationLanguageName().equals(languageName)) {
                return card;
            }
        }
        String errorMessage = String.format("Cannot find translation for %s in %s", sourcePhrase, languageName);
        throw new RuntimeException(errorMessage);
    }

    public boolean containsCardForLanguage(String language) {
        for (Card card : cards) {
            if(card.getDestinationLanguageName().equals(language)) {
                return true;
            }
        }
        return false;
    }
}