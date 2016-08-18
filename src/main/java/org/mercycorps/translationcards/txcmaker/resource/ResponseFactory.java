package org.mercycorps.translationcards.txcmaker.resource;

import org.springframework.stereotype.Service;

@Service
public class ResponseFactory {
    public ImportDeckResponse newImportDeckResponse() {
        return new ImportDeckResponse();
    }
}
