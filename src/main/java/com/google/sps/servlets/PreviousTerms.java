package com.google.sps.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/prev-terms")
public class PreviousTerms extends HttpServlet {
  private DatastoreService db = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Entity> prevTermsData = getPreviousTerms(db, request);
    String prevTermsDataJSON = makeJSON(prevTermsData);
    response.setContentType("application/json;");
    response.getWriter().println(prevTermsDataJSON);
  }

  private List<Entity> getPreviousTerms(DatastoreService db, HttpServletRequest request) {
    String schoolName = request.getParameter("school-name");
    String courseName = request.getParameter("course-name");
    Long units = Long.parseLong(request.getParameter("num-units"));
    Integer comparisonCount = Integer.parseInt(request.getParameter("term-limit"));

    Entity foundTerm = findTerm(db, request);
    Date startTime = (Date) foundTerm.getProperty("timeStamp");
    Key schoolKey = findQueryMatch(db, "School", "school-name", schoolName).get(0).getKey();

    List<Filter> filters = new ArrayList();
    Filter courseFilter = new FilterPredicate("course-name", FilterOperator.EQUAL, courseName);
    Filter unitFilter = new FilterPredicate("units", FilterOperator.EQUAL, units);
    filters.add(courseFilter);
    filters.add(unitFilter);

    Query courseQuery =
        new Query("Course").setAncestor(schoolKey).setFilter(CompositeFilterOperator.and(filters));
    Key courseKey =
        db.prepare(courseQuery).asList(FetchOptions.Builder.withDefaults()).get(0).getKey();

    Filter timeFilter = new FilterPredicate("timeStamp", FilterOperator.LESS_THAN, startTime);
    Query termQuery =
        new Query("Term")
            .setAncestor(courseKey)
            .addSort("timeStamp", SortDirection.DESCENDING)
            .setFilter(timeFilter);
    List<Entity> foundTerms =
        db.prepare(termQuery).asList(FetchOptions.Builder.withLimit(comparisonCount));
    return foundTerms;
  }

  // Guaranted to find a match given that the term page already exists
  // TODO: Once we refactor to use keys, have it throw an error if term not found
  private Entity findTerm(DatastoreService db, HttpServletRequest request) {
    String schoolName = request.getParameter("school-name");
    String courseName = request.getParameter("course-name");
    String termName = request.getParameter("term");
    Long units = Long.parseLong(request.getParameter("num-units"));

    Key schoolKey = findQueryMatch(db, "School", "school-name", schoolName).get(0).getKey();

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

  private List<Entity> findQueryMatch(
      DatastoreService db, String entityType, String entityProperty, String propertyValue) {
    Filter filter = new FilterPredicate(entityProperty, FilterOperator.EQUAL, propertyValue);
    Query q = new Query(entityType).setFilter(filter);
    List<Entity> result = db.prepare(q).asList(FetchOptions.Builder.withDefaults());
    return result;
  }

  private String makeJSON(Object changeItem) throws JsonProcessingException {
    try {
      ObjectMapper mapper = new ObjectMapper();
      String jsonString = mapper.writeValueAsString(changeItem);
      return jsonString;
    } catch (JsonProcessingException e) {
      throw e;
    }
  }
}
