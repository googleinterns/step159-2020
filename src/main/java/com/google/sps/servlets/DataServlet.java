package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
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
    Query query = new Query("Comments").addSort("score-class", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      String comment = (String) entity.getProperty("comments-class");
      json.add(comment);
    }

    // Send JSON string.
    String jsonVersion = new Gson().toJson(json);
    response.setContentType("application/json;");
    response.getWriter().println(jsonVersion);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get written feedback.
    String classFeedback = request.getParameter("class-input");
    String professorFeedback = request.getParameter("prof-input");
    // Get class/professor ratings.
    String ratingClass = request.getParameter("rating-class");
    String ratingProfessor = request.getParameter("rating-professor");
    boolean translateEnglish = Boolean.parseBoolean(request.getParameter("languages"));

    if (translateEnglish) {
      Translate translate = TranslateOptions.getDefaultInstance().getService();
      Translation translationClassFeedback =
          translate.translate(classFeedback, Translate.TranslateOption.targetLanguage("en"));
      Translation translationProfessorFeedback =
          translate.translate(professorFeedback, Translate.TranslateOption.targetLanguage("en"));
      String translatedClassFeedback = translationClassFeedback.getTranslatedText();
      String translatedProfessorFeedback = translationProfessorFeedback.getTranslatedText();
      classFeedback = translatedClassFeedback;
      professorFeedback = translatedProfessorFeedback;
    }

    Document docClass =
        Document.newBuilder().setContent(classFeedback).setType(Document.Type.PLAIN_TEXT).build();
    Document docProfessor =
        Document.newBuilder()
            .setContent(professorFeedback)
            .setType(Document.Type.PLAIN_TEXT)
            .build();

    LanguageServiceClient languageServiceForClass = LanguageServiceClient.create();
    Sentiment sentimentForClass =
        languageServiceForClass.analyzeSentiment(docClass).getDocumentSentiment();
    float scoreClass = sentimentForClass.getScore();
    languageServiceForClass.close();

    LanguageServiceClient languageServiceForProf = LanguageServiceClient.create();
    Sentiment sentimentForProf =
        languageServiceForProf.analyzeSentiment(docProfessor).getDocumentSentiment();
    float scoreProfessor = sentimentForProf.getScore();
    languageServiceForProf.close();

    Entity professorEntity = new Entity("Professor");
    professorEntity.setProperty("comments-professor", professorFeedback);
    professorEntity.setProperty("score-professor", scoreProfessor);
    professorEntity.setProperty("perception", ratingProfessor);
    
    Entity commentsEntity = new Entity("Comments");
    commentsEntity.setProperty("comments-class", classFeedback);
    commentsEntity.setProperty("score-class", scoreClass);
    commentsEntity.setProperty("perception", ratingClass);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentsEntity);
    datastore.put(professorEntity);

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
