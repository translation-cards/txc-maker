package org.mercycorps.translationcards.txcmaker.model;

import org.mercycorps.translationcards.txcmaker.task.TxcPortingUtility;

/**
 * Created by agarrard on 7/27/16.
 */
public class Card {

  public String card_label;
  public String dest_audio;
  public String dest_txt;

  public Card setLabel(String label) {
    this.card_label = label;
    return this;
  }

  public Card setFilename(String filename) {
    this.dest_audio = filename;
    return this;
  }

  public Card setTranslationText(String translationText) {
    this.dest_txt = translationText;
    return this;
  }
}
