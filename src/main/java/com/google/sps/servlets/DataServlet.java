package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
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
import com.google.appengine.api.datastore.Key;

/** An item on a todo list. */

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private final List<Object> commentsList = new ArrayList<>();
  private Key existingTermRatingKey;
  private Key existingProfRatingKey;
  private Key currentTermKey;

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
    Int classRating = request.getParameter("rating-class");
    Int workHours = request.getParameter("hoursOfWork");
    Int difficulty = request.getParameter("difficulty");
    String professorFeedback = request.getParameter("prof-input");
    Int professorRating = request.getParameter("rating-professor");
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
        Document.newBuilder()
            .setContent(classFeedbackDoc)
            .setType(Document.Type.PLAIN_TEXT)
            .build();
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
    Entity userID = new Entity("User");
    
    // User has not reviewed any rating.
    if (userQueryList.size() == 0) {
      Entity newReviewer = new Entity("User");
      newReviewer.setProperty("user-email", userEmail);
      Entity userID = newReviewer;
    } else {
      Entity userID = userQueryList.get(0);
    }
    
    currentTermKey = request.getParameter("term");
    Entity currentTerm = datastore.get(currentTermKey);
    currentProfKey = currentTerm.getProperty("Professor");
    
    // Checks for term rating, only need to query one as prof and term get reviewed simultaneously. 
    Filter reviewerFilter = new FilterPredicate("reviewer-id", FilterOperator.EQUAL, userID.getKey());
    Query ratingQuery = new Query("Rating").setAncestor(currentTermKey).setFilter(reviewerFilter);
    List<Entity> ratingQueryList =
          datastore.prepare(ratingQuery).asList(FetchOptions.Builder.withDefaults());

    if (ratingQueryList.size() != 0) {
      // A rating from this user for this term and professor has been found.
      Entity existingTermRatingEntity = ratingQueryList.get(0);
      existingTermRatingKey = existingTermRatingEntity.getKey();
      Entity updatedProfRatingEntity =
          Entity.newBuilder(datastore.get(currentProfKey))
              .set("comments-professor", professorFeedback)
              .set("score-professor", professorScore)
              .set("perception", professorRating)
              .set("difficulty", difficulty)
              .set("hours", workHours)
              .build();
      Entity updatedTermRatingEntity =
          Entity.newBuilder(datastore.get(existingTermRatingKey))
              .set("comments-class", classFeedback)
              .set("score-class", classScore)
              .set("perception", classRating)
              .set("difficulty", difficulty)
              .set("hours", workHours)
              .build();      
      datastore.update(updatedTermRatingEntity);
      datastore.update(updatedProfRatingEntity);
    } else {
      // A rating from this user has not been found, will be created. 
      Entity professorRatingEntity = new Entity("Rating", currentProfKey);
      professorRatingEntity.setProperty("comments-professor", professorFeedback);
      professorRatingEntity.setProperty("reviewer-id", userID.getKey());
      professorRatingEntity.setProperty("score-professor", professorScore);
      professorRatingEntity.setProperty("perception", professorRating);
      professorRatingEntity.setProperty("hours", workHours);
      professorRatingEntity.setProperty("difficulty", difficulty);

      Entity classRatingEntity = new Entity("Rating", currentTermKey);
      classRatingEntity.setProperty("comments-class", classFeedback);
      classRatingEntity.setProperty("reviewer-id", userID.getKey());
      classRatingEntity.setProperty("score-class", classScore);
      classRatingEntity.setProperty("perception", classRating);
      classRatingEntity.setProperty("hours", workHours);
      classRatingEntity.setProperty("difficulty", difficulty);

      datastore.put(classRatingEntity);
      datastore.put(professorRatingEntity);
    }

    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.sendRedirect("/index.html");
  }
}
