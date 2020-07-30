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
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private final List<Object> commentsList = new ArrayList<>();
  private Key currentTermKey;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Rating").addSort("score-class", SortDirection.ASCENDING);
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
    addTermRating(request);
    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.sendRedirect("/index.html");
  }

  public void addTermRating(HttpServletRequest request) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String classFeedback = request.getParameter("class-input");
    Long classRating = Integer.parseInt(request.getParameter("rating-class"));
    Long workHours = Integer.parseInt(request.getParameter("hoursOfWork"));
    Long difficulty = Integer.parseInt(request.getParameter("difficulty"));
    String professorFeedback = request.getParameter("prof-input");
    Long professorRating = Integer.parseInt(request.getParameter("rating-professor"));
    boolean translateToEnglish = Boolean.parseBoolean(request.getParameter("languages"));

    if (translateToEnglish) {
      classFeedback = translateFeedback(classFeedback);
      professorFeedback = translateFeedback(professorFeedback);
    }

    float classScore = getSentimentScore(classFeedback);
    float professorScore = getSentimentScore(professorFeedback);

    // Gets user email.
    String userId = request.getParameter("ID");
    // Gets term key from Course object.
    Key currentTermKey = request.getParameter("Course").term;
    Entity currentTerm = datastore.get(currentTermKey);

    // Check whether user has reviewed that term.
    List<Entity> termRatingQueryList = queryEntities("Rating", "reviewer-id", userId);

    Entity classRatingEntity =
        termRatingQueryList.isEmpty()
            ? new Entity("Rating", currentTermKey)
            : termRatingQueryList.get(0);

    classRatingEntity.setProperty("comments-class", classFeedback);
    classRatingEntity.setProperty("reviewer-id", userId);
    classRatingEntity.setProperty("score-class", classScore);
    classRatingEntity.setProperty("perception-class", classRating);
    classRatingEntity.setProperty("hours", workHours);
    classRatingEntity.setProperty("difficulty", difficulty);
    classRatingEntity.setProperty("comments-professor", professorFeedback);
    classRatingEntity.setProperty("score-professor", professorScore);
    classRatingEntity.setProperty("perception-professor", professorRating);
    datastore.put(classRatingEntity);
  }

  private String translateFeedback(String feedback) {
    Translate translateService = TranslateOptions.getDefaultInstance().getService();
    Translation translationFeedback =
        translateService.translate(feedback, Translate.TranslateOption.targetLanguage("en"));
    String translatedFeedback = translationFeedback.getTranslatedText();
    return translatedFeedback;
  }

  private float getSentimentScore(String feedback) {
    Document feedbackDoc =
        Document.newBuilder().setContent(feedback).setType(Document.Type.PLAIN_TEXT).build();

    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(feedbackDoc).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();
  }

  private List<Entity> queryEntities(String entityName, String propertyName, String propertyValue) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Filter filter = new FilterPredicate(propertyName, FilterOperator.EQUAL, propertyValue);
    Query query = new Query(entityName).setFilter(filter);
    // This is initialized when authentication happens, so should not be empty.
    List<Entity> queryList = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    return queryList;
  }
}
