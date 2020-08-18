package com.google.sps.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
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
    Key courseKey = KeyFactory.stringToKey(request.getProperty("course-key"));
    Key termKey = KeyFactory.stringToKey(request.getProperty("term-key"));
    Date startTime = db.get(termKey).getProperty("timeStamp");
    Integer termLimit = Integer.parseInt(request.getProperty("term-limit"));

    Filter timeFilter = new FilterPredicate("timeStamp", FilterOperator.LESS_THAN, startTime);
    Query termQuery =
        new Query("Term")
            .setAncestor(courseKey)
            .addSort("timeStamp", SortDirection.DESCENDING)
            .setFilter(timeFilter);
    List<Entity> foundTerms =
        db.prepare(termQuery).asList(FetchOptions.Builder.withLimit(termLimit));
    return foundTerms;
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
