package org.mercycorps.translationcards.txcmaker.model;

import java.util.*;

public class Deck {

  public String deck_label;
  public String publisher;
  public String iso_code;
  public String language_label;
  public String id;
  public long timestamp;
  public String license_url;
  public boolean locked;
  public List<Language> languages;
  private transient Map<String, Language> languageLookup;

  public Deck() {
    languages = new ArrayList<Language>();
    languageLookup = new HashMap<String, Language>();
  }

  public Deck setDeckLabel(String deckLabel) {
    this.deck_label = deckLabel;
    return this;
  }

  public Deck setPublisher(String publisher) {
    this.publisher = publisher;
    return this;
  }

  public Deck setLanguage(String iso_code) {
    this.iso_code = iso_code;
    return this;
  }

  public Deck setLanguageLabel(String language_label) {
    this.language_label = language_label;
    return this;
  }

  public Deck setDeckId(String deckId) {
    this.id = deckId;
    return this;
  }

  public Deck setTimestamp(long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public Deck setLicenseUrl(String licenseUrl) {
    this.license_url = licenseUrl;
    return this;
  }

  public Deck setLocked(boolean locked) {
    this.locked = locked;
    return this;
  }

  public Deck addCard(String iso_code, String language_label, Card card) {
    if (!languageLookup.containsKey(iso_code)) {
      Language langSpec = new Language(iso_code, language_label);
      languages.add(langSpec);
      languageLookup.put(iso_code, langSpec);
    }
    languageLookup.get(iso_code).addCard(card);
    return this;
  }

  public static Deck stub() {
    Map<String,String> languages = new HashMap<>();
    languages.put("ar", "Arabic");
    languages.put("fa", "Farsi");
    languages.put("ps", "Pushto");
    List<String> phrases = Arrays.asList(
            "Do you understand this language?",
            "Can I talk to you, using this mobile application (App)?",
            "What is your name?",
            "Do you need water?",
            "Do you have a phone?",
            "Do you need medical attention?",
            "Where do you come from?");

    Deck deck = new Deck()
            .setDeckId("1234")
            .setDeckLabel("Default Deck")
            .setPublisher("Women Hack Syria")
            .setLanguage("en")
            .setLanguageLabel("English")
            .setLocked(false)
            .setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
            .setTimestamp(System.currentTimeMillis());

    for(String iso_code : languages.keySet()) {
      for(String phrase : phrases) {
        String language_label = languages.get(iso_code);
        deck.addCard(iso_code, language_label, new Card()
                .setLabel(phrase)
                .setTranslationText(language_label + " translation")
                .setFilename(phrase + ".mp3"));
      }
    }
    return deck;
  }

}
