package org.mercycorps.translationcards.txcmaker.model;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.common.base.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
