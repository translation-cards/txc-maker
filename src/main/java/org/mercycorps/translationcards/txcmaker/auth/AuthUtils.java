package org.mercycorps.translationcards.txcmaker.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthUtils {

  private final AppEngineDataStoreFactory DATA_STORE_FACTORY =
      AppEngineDataStoreFactory.getDefaultInstance();
  
  final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();

  final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  final Collection<String> SCOPES = Arrays.asList(
      DriveScopes.DRIVE_READONLY, DriveScopes.DRIVE_FILE);

  private final String CLIENT_SECRETS_FILENAME = "/WEB-INF/client_secrets.json";

  public String getRedirectUri(HttpServletRequest req) {
    GenericUrl url = new GenericUrl(req.getRequestURL().toString());
    url.setRawPath("/oauth2callback");
    return url.build();
  }

  public Drive getDriveOrOAuth(
          ServletContext context, HttpServletRequest req, HttpServletResponse resp,
          boolean orOAuth, String sessionId)
      throws IOException {
    AuthorizationCodeFlow flow = newFlow(context);
    Credential credential = flow.loadCredential(sessionId);
    if (credential == null || credential.getExpiresInSeconds() < 0) {
      if (orOAuth) {
        String url = flow.newAuthorizationUrl()
            .setRedirectUri(getRedirectUri(req))
            .build(); 
        resp.sendRedirect(url);
      }
      return null;
    } else {
      return getDriveService(credential);
    }
  }

  public AuthorizationCodeFlow newFlow(ServletContext context) throws IOException {
    InputStream in = context.getResourceAsStream(CLIENT_SECRETS_FILENAME);
    GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    return new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, secrets, SCOPES)
        .setDataStoreFactory(DATA_STORE_FACTORY)
        .setAccessType("offline")
        .build();
  }

  Drive getDriveService(Credential credential) throws IOException {
    return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName("TXC Maker")
        .build();
  }
}
