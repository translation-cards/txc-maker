package org.mercycorps.translationcards.txcmaker.service;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

@Service
public class GcsStreamFactory {

    private static final String GCS_BUCKET_NAME = "translation-cards-dev.appspot.com";
    private static final int BUFFER_SIZE = 1024;

    private GcsService gcsService;

    @Autowired
    public GcsStreamFactory(GcsService gcsService) {
        this.gcsService = gcsService;
    }

    @Nullable
    public OutputStream getOutputStream(String fileName) {
        GcsFilename gcsFilename = new GcsFilename(GCS_BUCKET_NAME, fileName);
        OutputStream gcsOutput = null;
        try {
            gcsOutput = Channels.newOutputStream(
                    gcsService.createOrReplace(gcsFilename, GcsFileOptions.getDefaultInstance()));
        } catch(IOException e) {
            //do something
        }
        return gcsOutput;
    }

    public InputStream getInputStream(String fileName) {
        GcsFilename gcsFilename = new GcsFilename(GCS_BUCKET_NAME, fileName);
        return Channels.newInputStream(
                gcsService.openPrefetchingReadChannel(gcsFilename, 0, BUFFER_SIZE)
        );
    }
}
