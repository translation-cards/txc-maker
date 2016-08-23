package org.mercycorps.translationcards.txcmaker.response;

import org.springframework.stereotype.Service;

@Service
public class ResponseFactory {
    public ImportDeckResponse newImportDeckResponse() {
        return new ImportDeckResponse();
    }
}
