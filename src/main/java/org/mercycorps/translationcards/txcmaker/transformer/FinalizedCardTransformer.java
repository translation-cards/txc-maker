package org.mercycorps.translationcards.txcmaker.transformer;

import org.mercycorps.translationcards.txcmaker.model.FinalizedCard;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.springframework.stereotype.Service;

@Service
public class FinalizedCardTransformer {

    public FinalizedCard transform(Card card) {
        return new FinalizedCard().setCard_label(card.getSourcePhrase())
                .setDest_audio(card.getAudio())
                .setDest_txt(card.getDestinationPhrase());
    }
}
