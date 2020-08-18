package com.google.sps.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/prev-key")
public class PreviousTermKey extends HttpServlet {
  private DatastoreService db = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Key prevTermKey = getPreviousTermKey(db, request);
    String prevTermsKeyJSON = makeJSON(prevTermKey);
    response.setContentType("application/json;");
    response.getWriter().println(prevTermsKeyJSON);
  }

  private Key getPreviousTermKey(DatastoreService db, HttpServletRequest request) {
    Key courseKey = KeyFactory.stringToKey(request.getParameter("course-key"));
    Key termName = KeyFactory.stringToKey(request.getParameter("term"));
    Filter termFilter = new FilterPredicate("term", FilterOperator.EQUAL, termName);
    Query termQuery = new Query("Term").setAncestor(courseKey).setFilter(termFilter);
    return db.prepare(termQuery).asList(FetchOptions.Builder.withDefaults()).get(0).getKey();
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
