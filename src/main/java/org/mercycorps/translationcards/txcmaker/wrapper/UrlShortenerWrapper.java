package org.mercycorps.translationcards.txcmaker.wrapper;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.UrlshortenerRequestInitializer;
import com.google.api.services.urlshortener.model.Url;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UrlShortenerWrapper {

    @Value("${txcmaker-api-key}")
    private String API_KEY;

    public String getShortUrl(String longUrl) {
        Urlshortener shortener = newUrlshortener();
        Url toInsert = new Url().setLongUrl(longUrl);
        String shortUrl = longUrl;
        try {
            shortUrl = shortener.url().insert(toInsert).execute().getId();
        } catch (GoogleJsonResponseException e) {
            System.err.println(e.getDetails());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return shortUrl;
    }

    private Urlshortener newUrlshortener() {
        UrlshortenerRequestInitializer requestInitializer = new UrlshortenerRequestInitializer(API_KEY);
        return new Urlshortener.Builder(new UrlFetchTransport(), new JacksonFactory(), null)
                .setGoogleClientRequestInitializer(requestInitializer)
                .setApplicationName("txcmaker")
                .build();
    }
}
