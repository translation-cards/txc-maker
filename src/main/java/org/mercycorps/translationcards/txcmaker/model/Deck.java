package org.mercycorps.translationcards.txcmaker.model;

import java.util.*;

public class Deck {

  public String deck_label;
  public String publisher;
  public String iso_code;
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

  public Deck addCard(String language, Card card) {
    if (!languageLookup.containsKey(language)) {
      Language langSpec = new Language(language);
      languages.add(langSpec);
      languageLookup.put(language, langSpec);
    }
    languageLookup.get(language).addCard(card);
    return this;
  }

  public static Deck stub() {
    List<String> languages = Arrays.asList("ar", "fa", "ps");
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
            .setLocked(false)
            .setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
            .setTimestamp(System.currentTimeMillis());

    for(String language : languages) {
      for(String phrase : phrases) {
        deck.addCard(language, new Card()
                .setLabel(phrase)
                .setTranslationText(language + " translation")
                .setFilename(phrase + ".mp3"));
      }
    }
    return deck;
  }

}
