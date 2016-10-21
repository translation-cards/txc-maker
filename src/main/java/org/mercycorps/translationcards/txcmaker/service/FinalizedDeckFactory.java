package org.mercycorps.translationcards.txcmaker.service;

import org.mercycorps.translationcards.txcmaker.model.FinalizedDeck;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.transformer.FinalizedLanguageTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinalizedDeckFactory {

    private final FinalizedLanguageTransformer transformer;

    @Autowired
    public FinalizedDeckFactory(FinalizedLanguageTransformer transformer) {
        this.transformer = transformer;
    }

    public FinalizedDeck finalize(Deck deck) {
        FinalizedDeck finalizedDeck = new FinalizedDeck()
                .setDeck_label(deck.getDeckLabel())
                .setId(deck.getId())
                .setLicense_url(deck.getLicense())
                .setLocked(deck.getLocked())
                .setPublisher(deck.getPublisher())
                .setReadme("This deck was created with the TXC Maker tool. See https://github.com/translation-cards/txc-maker for more information.")
                .setTimestamp(deck.getTimestamp())
                .setSource_language(deck.getIsoCode());

        finalizedDeck.setLanguages(transformer.transform(deck));

        return finalizedDeck;
    }
}
