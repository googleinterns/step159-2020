import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/secure")
public class SecureVerificationServlet extends HttpServlet {
  private String clientId =
      "12523748853-6h3fnh1kppht9s895ist2b5222ifh2u7.apps.googleusercontent.com"; // Client ID.

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String idTokenString = request.getParameter("token");
    UrlFetchTransport transport = UrlFetchTransport.getDefaultInstance();
    GsonFactory json = GsonFactory.getDefaultInstance();
    GoogleIdTokenVerifier verifier =
        new GoogleIdTokenVerifier.Builder(transport, json)
            .setAudience(Collections.singletonList(clientId)) // Client ID.
            .build();
    try {
      GoogleIdToken idToken = verifier.verify(idTokenString);
      if (idToken != null) {
        Payload payload = idToken.getPayload();
        String userId = payload.getSubject();
        String userJson = new Gson().toJson(userId);
        response.setContentType("application/json;");
        response.getWriter().println(userJson);
      }
    } catch (GeneralSecurityException e) {
      response.getWriter().println("Unsecure email.");
    }
  }
}
