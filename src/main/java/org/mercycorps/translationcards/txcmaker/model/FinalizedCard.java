package org.mercycorps.translationcards.txcmaker.model;

public class FinalizedCard {
    public String dest_txt;
    public String sourcePhrase;
    public String dest_audio;

    public FinalizedCard setDest_txt(String dest_txt) {
        this.dest_txt = dest_txt;
        return this;
    }

    public FinalizedCard setSourcePhrase(String sourcePhrase) {
        this.sourcePhrase = sourcePhrase;
        return this;
    }

    public FinalizedCard setDest_audio(String dest_audio) {
        this.dest_audio = dest_audio;
        return this;
    }
}
