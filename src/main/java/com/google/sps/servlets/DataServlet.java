package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
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
  private final List<Object> commentsList = new ArrayList<>();
  private Key existingTermRatingKey;
  private Key currentProfKey;
  private Key currentTermKey;
  private Key currentSchoolKey;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comments").addSort("score-class", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      String comment = (String) entity.getProperty("comments-class");
      commentsList.add(comment);
    }

    // Send JSON string.
    String jsonVersionCommentsList = new Gson().toJson(commentsList);
    commentsList.clear();
    response.setContentType("application/json;");
    response.getWriter().println(jsonVersionCommentsList);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get written feedback.
    String classFeedback = request.getParameter("class-input");
    int classRating = Integer.parseInt(request.getParameter("rating-class"));
    int workHours = Integer.parseInt(request.getParameter("hoursOfWork"));
    int difficulty = Integer.parseInt(request.getParameter("difficulty"));
    String professorFeedback = request.getParameter("prof-input");
    int professorRating = Integer.parseInt(request.getParameter("rating-professor"));
    boolean translateToEnglish = Boolean.parseBoolean(request.getParameter("languages"));

    if (translateToEnglish) {
      Translate translateService = TranslateOptions.getDefaultInstance().getService();
      Translation translationClassFeedback =
          translateService.translate(classFeedback, Translate.TranslateOption.targetLanguage("en"));
      Translation translationProfessorFeedback =
          translateService.translate(
              professorFeedback, Translate.TranslateOption.targetLanguage("en"));
      String translatedClassFeedback = translationClassFeedback.getTranslatedText();
      String translatedProfessorFeedback = translationProfessorFeedback.getTranslatedText();
      classFeedback = translatedClassFeedback;
      professorFeedback = translatedProfessorFeedback;
    }

    Document classFeedbackDoc =
        Document.newBuilder().setContent(classFeedback).setType(Document.Type.PLAIN_TEXT).build();
    Document professorFeedbackDoc =
        Document.newBuilder()
            .setContent(professorFeedback)
            .setType(Document.Type.PLAIN_TEXT)
            .build();

    LanguageServiceClient classLanguageService = LanguageServiceClient.create();
    Sentiment classSentiment =
        classLanguageService.analyzeSentiment(classFeedbackDoc).getDocumentSentiment();
    float classScore = classSentiment.getScore();
    classLanguageService.close();

    LanguageServiceClient profLanguageService = LanguageServiceClient.create();
    Sentiment professorSentiment =
        profLanguageService.analyzeSentiment(professorFeedbackDoc).getDocumentSentiment();
    float professorScore = professorSentiment.getScore();
    profLanguageService.close();

    // Gets user email.
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();

    // Check whether user has reviewer ID in system.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Filter userFilter = new FilterPredicate("user-email", FilterOperator.EQUAL, userEmail);
    Query userQuery = new Query("User").setFilter(userFilter);
    List<Entity> userQueryList =
        datastore.prepare(userQuery).asList(FetchOptions.Builder.withDefaults());
    Entity userEntity = new Entity("User");

    // User has not reviewed any rating.
    if (userQueryList.isEmpty()) {
      Entity newReviewer = new Entity("User");
      newReviewer.setProperty("user-email", userEmail);
      userEntity = newReviewer;
    } else {
      userEntity = userQueryList.get(0);
    }

    // Has to be added to URL.
    currentTermKey = request.getParameter("term");
    currentSchoolKey = request.getParameter("school");
    Entity currentTerm = datastore.get(currentTermKey);
    currentProfKey = currentTerm.getProperty("professor");

    // Checks for term rating by reviewer.
    Filter termReviewerFilter =
        new FilterPredicate("reviewer-id", FilterOperator.EQUAL, userEntity.getKey());
    Query termRatingQuery =
        new Query("Rating Term").setAncestor(currentTermKey).setFilter(termReviewerFilter);
    List<Entity> termRatingQueryList =
        datastore.prepare(termRatingQuery).asList(FetchOptions.Builder.withDefaults());

    // Checks for professor rating by reviewer.
    Filter profReviewerFilter =
        new FilterPredicate("reviewer-id", FilterOperator.EQUAL, userEntity.getKey());
    Query profRatingQuery =
        new Query("Rating Professor").setAncestor(currentSchoolKey).setFilter(profReviewerFilter);
    List<Entity> profRatingQueryList =
        datastore.prepare(profRatingQuery).asList(FetchOptions.Builder.withDefaults());

    if (termRatingQueryList.isEmpty()) {
      Entity classRatingEntity = new Entity("Rating Term", currentTermKey);
      classRatingEntity.setProperty("comments-class", classFeedback);
      classRatingEntity.setProperty("reviewer-id", userEntity.getKey());
      classRatingEntity.setProperty("score-class", classScore);
      classRatingEntity.setProperty("perception", classRating);
      classRatingEntity.setProperty("hours", workHours);
      classRatingEntity.setProperty("difficulty", difficulty);
      datastore.put(classRatingEntity);
    } else {
      Entity updatedTermRatingEntity = termRatingQueryList.get(0);
      updatedTermRatingEntity.setProperty("comments-class", classFeedback);
      updatedTermRatingEntity.setProperty("reviewer-id", userEntity.getKey());
      updatedTermRatingEntity.setProperty("score-class", classScore);
      updatedTermRatingEntity.setProperty("perception", classRating);
      updatedTermRatingEntity.setProperty("difficulty", difficulty);
      updatedTermRatingEntity.setProperty("hours", workHours);
      datastore.put(updatedTermRatingEntity);
    }
    if (profRatingQueryList.isEmpty()) {
      Entity professorRatingEntity = new Entity("Rating Professor", currentProfKey);
      professorRatingEntity.setProperty("comments-professor", professorFeedback);
      professorRatingEntity.setProperty("reviewer-id", userEntity.getKey());
      professorRatingEntity.setProperty("score-professor", professorScore);
      professorRatingEntity.setProperty("perception", professorRating);
      datastore.put(professorRatingEntity);
    } else {
      Entity updatedProfRatingEntity = profRatingQueryList.get(0);
      updatedProfRatingEntity.setProperty("comments-professor", professorFeedback);
      updatedProfRatingEntity.setProperty("reviewer-idFuser", userEntity.getKey());
      updatedProfRatingEntity.setProperty("score-professor", professorScore);
      updatedProfRatingEntity.setProperty("perception", professorRating);
      datastore.put(updatedProfRatingEntity);
    }
    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.sendRedirect("/index.html");
  }
}
