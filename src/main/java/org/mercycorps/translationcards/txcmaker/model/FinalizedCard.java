package org.mercycorps.translationcards.txcmaker.model;

public class FinalizedCard {
    public String dest_txt;
    public String card_label;
    public String dest_audio;

    public FinalizedCard setDest_txt(String dest_txt) {
        this.dest_txt = dest_txt;
        return this;
    }

    public FinalizedCard setCard_label(String card_label) {
        this.card_label = card_label;
        return this;
    }

    public FinalizedCard setDest_audio(String dest_audio) {
        this.dest_audio = dest_audio;
        return this;
    }
}
