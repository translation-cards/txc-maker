package org.mercycorps.translationcards.txcmaker.model;

import java.util.ArrayList;
import java.util.List;

public class Language {

  public String iso_code;
  public List<Card> cards;

  public Language(String isoCode) {
    this.iso_code = isoCode;
    cards = new ArrayList<Card>();
  }

  public Language addCard(Card card) {
    cards.add(card);
    return this;
  }
}
