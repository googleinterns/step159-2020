package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import com.google.sps.data.CommentClass;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** An item on a todo list. */

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private final List<Object> json = new ArrayList<>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int quantity = Integer.parseInt(request.getParameter("amount"));
    Query query = new Query("Comments").addSort("commentInstance", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results =
        datastore.prepare(query).asList(FetchOptions.Builder.withLimit(quantity));

    for (Entity entity : results) {
      String comment = (String) entity.getProperty("commentInstance");
      json.add(comment);
    }

    // Send JSON string.
    String jsonVersion = new Gson().toJson(json);
    response.setContentType("application/json;");
    response.getWriter().println(jsonVersion);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String text = request.getParameter("word-input");
    String status = request.getParameter("status");
    String language = request.getParameter("languages");

    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();

    Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();

    Translate translate = TranslateOptions.getDefaultInstance().getService();
    Translation translation =
        translate.translate(text, Translate.TranslateOption.targetLanguage(language));
    String finalText = translation.getTranslatedText();

    CommentClass commentInstance = new CommentClass(finalText, score);
    String commentInstanceJSON = new Gson().toJson(commentInstance);

    Entity taskEntity = new Entity("Comments");
    taskEntity.setProperty("commentInstance", commentInstanceJSON);
    taskEntity.setProperty("email", email);

    // Storing comments in their respective bins.
    if (status.equals("positive")) {
      taskEntity.setProperty("status", "positive");
    } else if (status.equals("negative")) {
      taskEntity.setProperty("status", "negative");
    } else if (status.equals("mixed")) {
      taskEntity.setProperty("status", "mixed");
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.sendRedirect("/index.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter was not specified by the
   *     client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    return value != null ? value : defaultValue;
  }
}
