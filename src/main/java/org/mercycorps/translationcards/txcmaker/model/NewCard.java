package org.mercycorps.translationcards.txcmaker.model;

import java.util.List;

public class NewCard {

    private String sourcePhrase;
    private String destinationAudioFilename;
    private String destinationPhrase;
    private List<Error> errors;
    private Language destinationLanguage;

    public NewCard(String sourcePhrase, String destinationAudioFilename, String destinationPhrase, List<Error> errors, Language destinationLanguage) {
        this.sourcePhrase = sourcePhrase;
        this.destinationAudioFilename = destinationAudioFilename;
        this.destinationPhrase = destinationPhrase;
        // TODO: we might need to set errors programmatically, rather than inject it
        this.errors = errors;
        this.destinationLanguage = destinationLanguage;
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
