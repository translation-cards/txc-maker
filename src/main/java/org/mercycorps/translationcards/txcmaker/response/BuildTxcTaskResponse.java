package org.mercycorps.translationcards.txcmaker.response;

import org.mercycorps.translationcards.txcmaker.model.NewDeck;

public class BuildTxcTaskResponse {

    private NewDeck deck;
    private String downloadUrl;

    public NewDeck getDeck() {
        return deck;
    }

    public BuildTxcTaskResponse setDeck(NewDeck deck) {
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
