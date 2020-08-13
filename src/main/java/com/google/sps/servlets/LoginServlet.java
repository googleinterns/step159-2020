package com.google.sps.servlets;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.gson.GsonFactory;
import com.google.sps.data.LoginObject;
import java.io.IOException;
import java.security.GeneralSecurityException;
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

  public LoginObject verifyToken(HttpServletRequest request) throws IOException {
    String idTokenString = request.getParameter("token");
    UrlFetchTransport transport = UrlFetchTransport.getDefaultInstance();
    GsonFactory json = GsonFactory.getDefaultInstance();
    GoogleIdTokenVerifier verifier =
        new GoogleIdTokenVerifier.Builder(transport, json)
            .setAudience(Collections.singletonList(clientId))
            .build();
    try {
      GoogleIdToken idToken = verifier.verify(idTokenString);
      if (idToken != null) {
        Payload payload = idToken.getPayload();
        String userId = payload.getSubject();
        return new LoginObject(userId, true, "");
      }
      return new LoginObject("", false, "NULL TOKEN");
    } catch (GeneralSecurityException e) {
      return new LoginObject("", false, "SECURITY ERROR");
    } catch (IllegalArgumentException e) {
      return new LoginObject("", false, "ARGUMENT ERROR");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    LoginObject login = verifyToken(request);
    JSONObject json = new JSONObject();
    json.put("id", login.getId());
    json.put("verified", login.getSuccess());
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
