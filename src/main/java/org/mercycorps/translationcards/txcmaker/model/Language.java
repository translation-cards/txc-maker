package org.mercycorps.translationcards.txcmaker.model;

public class Language {

    public String iso_code;
    public String language_label;

    public Language() {
        iso_code = "";
        language_label = "";
    }

    public Language(String isoCode, String language_label) {
        this.iso_code = isoCode;
        this.language_label = language_label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Language language = (Language) o;

        return language_label != null ? language_label.equals(language.language_label) : language.language_label == null;

    }

    @Override
    public int hashCode() {
        return language_label != null ? language_label.hashCode() : 0;
    }
}
