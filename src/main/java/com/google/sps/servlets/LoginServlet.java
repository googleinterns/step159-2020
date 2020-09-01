package com.google.sps.servlets;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.gson.GsonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.Collections;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  private String clientId =
      "12523748853-6h3fnh1kppht9s895ist2b5222ifh2u7.apps.googleusercontent.com"; // Client ID.
  private List<String> whitelist =
      Arrays.asList(
          "ninaprabhu@google.com",
          "lqueipom@google.com",
          "dagmawi@google.com",
          "crumley@google.com",
          "lgmt@google.com");

  public JSONObject verifyToken(HttpServletRequest request) throws IOException {
    String idTokenString = request.getParameter("token");
    UrlFetchTransport transport = UrlFetchTransport.getDefaultInstance();
    GsonFactory json = GsonFactory.getDefaultInstance();
    GoogleIdTokenVerifier verifier =
        new GoogleIdTokenVerifier.Builder(transport, json)
            .setAudience(Collections.singletonList(clientId))
            .build();

    JSONObject response = new JSONObject();

    try {
      GoogleIdToken idToken = verifier.verify(idTokenString);
      if (idToken != null) {
        Payload payload = idToken.getPayload();
        String userId = payload.getSubject();
        response.put("userId", userId);
        response.put("verified", "true");
        if (whitelist.contains(payload.getEmail())) {
          response.put("whitelist", true);
        } else {
          response.put("whitelist", false);
        }
        return response;
      } else {
        response.put("verified", false);
        response.put("whitelist", false);
        return response;
      }
    } catch (GeneralSecurityException e) {
      response.put("verified", "false");
      response.put("whitelist", "false");
      response.put("message", "SECURITY ERROR");
      return response;
    } catch (IllegalArgumentException e) {
      response.put("verified", "false");
      response.put("whitelist", "false");
      response.put("message", "ARGUMENT ERROR");
      return response;
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JSONObject json = verifyToken(request);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
