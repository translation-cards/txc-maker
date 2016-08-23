package org.mercycorps.translationcards.txcmaker.response;

import org.mercycorps.translationcards.txcmaker.model.Deck;

public class BuildTxcTaskResponse {

    private Deck deck;
    private String downloadUrl;

    public Deck getDeck() {
        return deck;
    }

    public BuildTxcTaskResponse setDeck(Deck deck) {
        this.deck = deck;
        return this;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public BuildTxcTaskResponse setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }
}
