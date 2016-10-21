package org.mercycorps.translationcards.txcmaker.model;

import java.util.ArrayList;
import java.util.List;

public class Card {

    private String sourcePhrase;
    private String destinationAudioFilename;
    private String destinationPhrase;
    private List<Error> errors;
    private Language destinationLanguage;

    // Required by gson
    private Card() {}

    public Card(String sourcePhrase, String destinationAudioFilename, String destinationPhrase, Language destinationLanguage) {
        this.sourcePhrase = sourcePhrase;
        this.destinationAudioFilename = destinationAudioFilename;
        this.destinationPhrase = destinationPhrase;
        this.destinationLanguage = destinationLanguage;
        this.errors = new ArrayList<>();
    }

    public String getDestinationLanguageName() {
        return destinationLanguage.language_label;
    }

    public String getSourcePhrase() {
        return sourcePhrase;
    }

    public String getAudio() {
        return destinationAudioFilename;
    }

    public String getDestinationPhrase() {
        return destinationPhrase;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public Language getDestinationLanguage() {
        return destinationLanguage;
    }
}
