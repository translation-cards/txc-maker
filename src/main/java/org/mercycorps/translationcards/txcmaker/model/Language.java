package org.mercycorps.translationcards.txcmaker.model;

import org.mercycorps.translationcards.txcmaker.task.TxcPortingUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agarrard on 7/27/16.
 */
public class Language {

  private String iso_code;
  private List<Card> cards;

  public Language(String isoCode) {
    this.iso_code = isoCode;
    cards = new ArrayList<Card>();
  }

  public Language addCard(Card card) {
    cards.add(card);
    return this;
  }
}
