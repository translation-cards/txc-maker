package org.mercycorps.translationcards.txcmaker.response;

import org.mercycorps.translationcards.txcmaker.model.FinalizedDeck;

public class BuildTxcTaskResponse {

    private FinalizedDeck deck;
    private String downloadUrl;

    public FinalizedDeck getDeck() {
        return deck;
    }

    public BuildTxcTaskResponse setDeck(FinalizedDeck deck) {
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
