package org.mercycorps.translationcards.txcmaker.model.importDeckForm;

import org.mercycorps.translationcards.txcmaker.model.Error;

import java.util.List;

public interface Field {
    List<Error> verify();
}
