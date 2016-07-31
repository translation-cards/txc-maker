package org.mercycorps.translationcards.txcmaker.model;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

public class ImportDeckForm {

    MultivaluedMap<String,String> formInput;

    public ImportDeckForm(MultivaluedMap<String, String> formInput) {
        this.formInput = formInput;
    }

    public Deck getDeck() {
        return new Deck()
                .setDeckLabel(getField("deckName"))
                .setPublisher(getField("publisher"))
                .setLicenseUrl(getField("licenseUrl"))
                .setLocked(Boolean.parseBoolean(getField("locked")))
                .setDeckId(getField("deckId"));
    }

    public String getAudioDirectoryId() {
        return getField("audioDirId");
    }

    public String getDocumentId() {
        return getField("docId");
    }

    private String getField(String key) {
        List<String> values = formInput.get(key);
        return values == null ? "" : values.get(0);
    }
}
