package org.mercycorps.translationcards.txcmaker.service;

import org.mercycorps.translationcards.txcmaker.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FinalizedDeckFactory {

    public FinalizedDeck finalize(NewDeck deck) {
        FinalizedDeck finalizedDeck = new FinalizedDeck()
                .setDeck_label(deck.getDeckLabel())
                .setId(deck.getId())
                .setLicense_url(deck.getLicense())
                .setLocked(deck.getLocked())
                .setPublisher(deck.getPublisher())
                .setReadme("This deck was created with the TXC Maker tool. See https://github.com/translation-cards/txc-maker for more information.")
                .setTimestamp(deck.getTimestamp())
                .setSource_language(deck.getIsoCode());

        List<FinalizedLanguage> finalizedLanguages = new ArrayList<>();
        // TODO: this method needs to set the finalizedLanguages from the NewDeck class..
        // TODO: we probably need to inject our language iso-code lookup service here so
        // TODO: we can programmatically create languages from the decks destination language field
//        for(Language language : deck.getDestinationLanguages()) {
//            finalizedLanguages.add(getFinalizedLanguage(language));
//        }
        finalizedDeck.setLanguages(finalizedLanguages);

        return finalizedDeck;
    }

    private FinalizedLanguage getFinalizedLanguage(Language language) {
        final FinalizedLanguage finalizedLanguage = new FinalizedLanguage().setIso_code(language.iso_code);

        List<FinalizedCard> finalizedCards = new ArrayList<>();
        // TODO: need to get all the cards for a specific language here for the finalization of the .txc file
//        for(NewCard card : language.getCards()) {
//            finalizedCards.add(getFinalizedCard(card));
//        }
        finalizedLanguage.setCards(finalizedCards);

        return finalizedLanguage;
    }

    private FinalizedCard getFinalizedCard(NewCard card) {
        return new FinalizedCard()
                .setCard_label(card.getSourcePhrase())
                .setDest_audio(card.getAudio())
                .setDest_txt(card.getDestinationPhrase());
    }
}
