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
  private final LanguageServiceClient languageService;
  private final DatastoreService db;

  public DataServlet() throws IOException {
    this.languageService = LanguageServiceClient.create();
  }

  public DataServlet(LanguageServiceClient languageService) {
    this.languageService = languageService;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Rating").addSort("score-term", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      String comment = (String) entity.getProperty("comments-term");
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

  public void addTermRating(HttpServletRequest request) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String termFeedback = request.getParameter("term-input");
    Long termRating = Long.parseLong(request.getParameter("rating-term"));
    Long workHours = Long.parseLong(request.getParameter("hoursOfWork"));
    Long difficulty = Long.parseLong(request.getParameter("difficulty"));
    String professorFeedback = request.getParameter("prof-input");
    Long professorRating = Long.parseLong(request.getParameter("rating-professor"));

    float termScore = getSentimentScore(termFeedback);
    float professorScore = getSentimentScore(professorFeedback);

    // Gets user ID from URL.
    String userId = request.getParameter("ID");
    // Quick change, will modify tests as well after the demo.
    Key currentKey =
        findTerm(
                db,
                request.getParameter("school-name"),
                request.getParameter("course-name"),
                request.getParameter("term"),
                Long.parseLong(request.getParameter("num-units")),
                request.getParameter("prof-name"))
            .getKey();

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

  private float getSentimentScore(String feedback) throws IOException {
    Document feedbackDoc =
        Document.newBuilder().setContent(feedback).setType(Document.Type.PLAIN_TEXT).build();
    Sentiment sentiment = this.languageService.analyzeSentiment(feedbackDoc).getDocumentSentiment();
    float score = sentiment.getScore();
    this.languageService.close();
    return score;
  }

  private Entity findTerm(
      DatastoreService db,
      String schoolName,
      String courseName,
      String termName,
      Long units,
      String profName) {
    Key schoolKey = queryEntities(db, "School", "school-name", schoolName).get(0).getKey();

    List<Filter> filters = new ArrayList();
    Filter courseFilter = new FilterPredicate("course-name", FilterOperator.EQUAL, courseName);
    Filter unitFilter = new FilterPredicate("units", FilterOperator.EQUAL, units);
    filters.add(courseFilter);
    filters.add(unitFilter);

    Query courseQuery =
        new Query("Course").setAncestor(schoolKey).setFilter(CompositeFilterOperator.and(filters));
    Key courseKey =
        db.prepare(courseQuery).asList(FetchOptions.Builder.withDefaults()).get(0).getKey();

    Filter termFilter = new FilterPredicate("term", FilterOperator.EQUAL, termName);
    Query termQuery = new Query("Term").setAncestor(courseKey).setFilter(termFilter);
    Entity foundTerm = db.prepare(termQuery).asList(FetchOptions.Builder.withDefaults()).get(0);

    return foundTerm;
  }

  public List<Entity> queryEntities(String entityName, String propertyName, String propertyValue)
      throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Filter filter = new FilterPredicate(propertyName, FilterOperator.EQUAL, propertyValue);
    Query query = new Query(entityName).setFilter(filter);
    // This is initialized when authentication happens, so should not be empty.
    List<Entity> queryList = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    return queryList;
  }
}
