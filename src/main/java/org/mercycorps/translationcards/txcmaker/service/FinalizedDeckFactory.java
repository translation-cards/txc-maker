package org.mercycorps.translationcards.txcmaker.service;

import org.mercycorps.translationcards.txcmaker.model.*;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FinalizedDeckFactory {

    public FinalizedDeck finalize(Deck deck) {
        FinalizedDeck finalizedDeck = new FinalizedDeck()
                .setDeck_label(deck.deck_label)
                .setId(deck.id)
                .setLicense_url(deck.license_url)
                .setLocked(deck.locked)
                .setPublisher(deck.publisher)
                .setReadme("This deck was created with the TXC Maker tool. See https://github.com/translation-cards/txc-maker for more information.")
                .setTimestamp(deck.timestamp)
                .setSource_language(deck.iso_code);

        List<FinalizedLanguage> finalizedLanguages = new ArrayList<>();
        for(Language language : deck.languages) {
            finalizedLanguages.add(getFinalizedLanguage(language));
        }
        finalizedDeck.setLanguages(finalizedLanguages);

        return finalizedDeck;
    }

    private FinalizedLanguage getFinalizedLanguage(Language language) {
        final FinalizedLanguage finalizedLanguage = new FinalizedLanguage()
                .setIso_code(language.iso_code);

        List<FinalizedCard> finalizedCards = new ArrayList<>();
        for(Card card : language.cards) {
            finalizedCards.add(getFinalizedCard(card));
        }
        finalizedLanguage.setCards(finalizedCards);

        return finalizedLanguage;
    }

    private FinalizedCard getFinalizedCard(Card card) {
        return new FinalizedCard()
                .setCard_label(card.card_label)
                .setDest_audio(card.dest_audio)
                .setDest_txt(card.dest_txt);
    }
}
