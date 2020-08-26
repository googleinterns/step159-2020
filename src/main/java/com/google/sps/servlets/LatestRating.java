package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/latest-rating")
public class LatestRating extends HttpServlet {
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JSONObject previousRatingInfo = new JSONObject();
    Key parentTermKey = KeyFactory.stringToKey((String) request.getParameter("term-key"));
    String userId = (String) request.getParameter("reviewer-id");
    List<Entity> latestRatingQuery = getLatestRating(userId, parentTermKey);

    if (!latestRatingQuery.isEmpty()) {
      Entity previousRatingEntity = latestRatingQuery.get(0);
      previousRatingInfo.put("termInput", previousRatingEntity.getProperty("comments-term"));
      previousRatingInfo.put("profInput", previousRatingEntity.getProperty("comments-professor"));
      previousRatingInfo.put("difficulty", previousRatingEntity.getProperty("difficulty"));
      previousRatingInfo.put("hours", previousRatingEntity.getProperty("hours"));
      previousRatingInfo.put("ratingTerm", previousRatingEntity.getProperty("perception-term"));
      previousRatingInfo.put(
          "ratingProf", previousRatingEntity.getProperty("perception-professor"));
      previousRatingInfo.put("grade", previousRatingEntity.getProperty("grade"));
    }
    response.setContentType("application/json;");
    response.getWriter().println(previousRatingInfo);
  }

  private List<Entity> getLatestRating(String reviewerId, Key termKey) throws IOException {
    Filter ratingFilter = new FilterPredicate("reviewer-id", FilterOperator.EQUAL, reviewerId);
    Query ratingQuery = new Query("Rating").setAncestor(termKey).setFilter(ratingFilter);
    return datastore.prepare(ratingQuery).asList(FetchOptions.Builder.withDefaults());
  }
}
