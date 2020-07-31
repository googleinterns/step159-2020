// package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** An item on a todo list. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private final Hashtable<String, String> hashtable = new Hashtable<String, String>();
  //   private Key existingTermRatingKey;
  //   private Key currentProfKey;
  //   private Key currentTermKey;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Rating").addSort("score-term", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> ratingQueryList =
        datastore.prepare(ratingQuery).asList(FetchOptions.Builder.withDefaults());

    if (!ratingQueryList.isEmpty()) {
      // Put list empty, check in JS if it is empty and print message if no other rating
      // If not, put things in list yay :)
      Entity latestRating = ratingQueryList.get(0);
      hashtable.put("comments-term", latestRating.getProperty("comments-term"));
      hashtable.put(
          "perception-term", String.parseString(latestRating.getProperty("perception-term")));
      hashtable.put("hours", String.parseString(latestRating.getProperty("hours")));
      hashtable.put("difficulty", String.parseString(latestRating.getProperty(difficulty)));
      hashtable.put("comments-professor", latestRating.getProperty(comments - professor));
      hashtable.put("perception-professor", latestRating.getProperty(perception - professor));
    }

    // Send JSON string.
    String jsonVersionHashtable = new Gson().toJson(hashtable);
    ratingQueryList.clear();
    response.setContentType("application/json;");
    response.getWriter().println(jsonVersionHashtable);
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
    String termFeedback = request.getParameter("term-input");
    Long termRating = Integer.parseInt(request.getParameter("rating-term"));
    Long workHours = Integer.parseInt(request.getParameter("hoursOfWork"));
    Long difficulty = Integer.parseInt(request.getParameter("difficulty"));
    String professorFeedback = request.getParameter("prof-input");
    Long professorRating = Integer.parseInt(request.getParameter("rating-professor"));
    boolean translateToEnglish = Boolean.parseBoolean(request.getParameter("languages"));

    if (translateToEnglish) {
      termFeedback = translateFeedback(termFeedback);
      professorFeedback = translateFeedback(professorFeedback);
    }

    float termScore = getSentimentScore(termFeedback);
    float professorScore = getSentimentScore(professorFeedback);

    // Gets user email.
    String userId = request.getParameter("ID");
    // Gets term key from Course object.
    Key currentTermKey = request.getParameter("Course").term;
    Entity currentTerm = datastore.get(currentTermKey);

    // Check whether user has reviewed that term.
    List<Entity> termRatingQueryList = queryEntities("Rating", "reviewer-id", userId);

    Entity termRatingEntity =
        termRatingQueryList.isEmpty()
            ? new Entity("Rating", currentTermKey)
            : termRatingQueryList.get(0);

    termRatingEntity.setProperty("comments-term", termFeedback);
    termRatingEntity.setProperty("reviewer-id", userId);
    termRatingEntity.setProperty("score-term", termScore);
    termRatingEntity.setProperty("perception-term", termRating);
    termRatingEntity.setProperty("hours", workHours);
    termRatingEntity.setProperty("difficulty", difficulty);
    termRatingEntity.setProperty("comments-professor", professorFeedback);
    termRatingEntity.setProperty("score-professor", professorScore);
    termRatingEntity.setProperty("perception-professor", professorRating);
    datastore.put(termRatingEntity);
  }

  private String translateFeedback(String feedback) {
    Translate translateService =
        TranslateOptions.newBuilder()
            .setProjectId("nina-laura-dagm-step-2020")
            .setQuotaProjectId("nina-laura-dagm-step-2020")
            .build()
            .getService();
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
