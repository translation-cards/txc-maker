package org.mercycorps.translationcards.txcmaker.transformer;

import org.mercycorps.translationcards.txcmaker.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class has the responsibility of taking in a {@link org.mercycorps.translationcards.txcmaker.model.NewDeck} and
 * transforming it into a list of {@link org.mercycorps.translationcards.txcmaker.model.FinalizedLanguage}s.
 */

@Service
public class FinalizedLanguageTransformer {

    private final FinalizedCardTransformer cardTransformer;

    @Autowired
    public FinalizedLanguageTransformer(FinalizedCardTransformer cardTransformer) {
        this.cardTransformer = cardTransformer;
    }

    public List<FinalizedLanguage> transform(NewDeck deck) {
        Map<Language, List<FinalizedCard>> cardsByLanguage = organizeCardsByLanguage(deck.getTranslations());
        return finalizeLanguages(cardsByLanguage);
    }

    Map<Language, List<FinalizedCard>> organizeCardsByLanguage(List<Translation> translations) {
        Map<Language, List<FinalizedCard>> cardsByLanguage = new HashMap<>();
        for (Translation translation : translations) {
            for (NewCard card : translation.getCards()) {
                if (!cardsByLanguage.containsKey(card.getDestinationLanguage())) {
                    cardsByLanguage.put(card.getDestinationLanguage(), new ArrayList<FinalizedCard>());
                }
                FinalizedCard finalizedCard = cardTransformer.transform(card);
                cardsByLanguage.get(card.getDestinationLanguage()).add(finalizedCard);
            }
        }
        return cardsByLanguage;
    }

    List<FinalizedLanguage> finalizeLanguages(Map<Language, List<FinalizedCard>> cardsByLanguage) {
        ArrayList<FinalizedLanguage> finalizedLanguages = new ArrayList<>();
        for (Entry<Language, List<FinalizedCard>> cardsByLanguageEntry : cardsByLanguage.entrySet()) {
            FinalizedLanguage finalizedLanguage = new FinalizedLanguage()
                    .setIso_code(cardsByLanguageEntry.getKey().iso_code)
                    .setCards(cardsByLanguageEntry.getValue());
            finalizedLanguages.add(finalizedLanguage);
        }
        return finalizedLanguages;
    }
}
