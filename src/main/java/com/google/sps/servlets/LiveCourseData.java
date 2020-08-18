package com.google.sps.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.TermDataHolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/term-data")
public class LiveCourseData extends HttpServlet {
  private DatastoreService db = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    TermDataHolder coursePageData = getAllDataFromTerm(db, request);
    String coursePageDataJSON = makeJSON(coursePageData);
    response.setContentType("application/json;");
    response.getWriter().println(coursePageDataJSON);
  }

  public TermDataHolder getAllDataFromTerm(DatastoreService db, HttpServletRequest request) {
    TermDataHolder termDataHolder = new TermDataHolder();
    Key termKey = KeyFactory.stringToKey(request.getProperty("term-key"));

    termDataHolder.setHoursList(getDataFromTermRating(db, foundTerm, "hours"));
    termDataHolder.setDifficultyList(getDataFromTermRating(db, foundTerm, "difficulty"));
    termDataHolder.setTermScoreList(getDataFromTermRating(db, foundTerm, "score-term"));
    termDataHolder.setTermPerceptionList(getDataFromTermRating(db, foundTerm, "perception-term"));
    termDataHolder.setProfessorPerceptionList(
        getDataFromTermRating(db, foundTerm, "perception-professor"));
    termDataHolder.setProfessorScoreList(getDataFromTermRating(db, foundTerm, "score-professor"));
    termDataHolder.setTermCommentsList(getDataFromTermRating(db, foundTerm, "comments-term"));
    termDataHolder.setProfessorCommentsList(
        getDataFromTermRating(db, foundTerm, "comments-professor"));
    return termDataHolder;
  }

  private List<Object> getDataFromTermRating(DatastoreService db, Key termKey, String property) {
    List<Object> dataList = new ArrayList();
    List<Entity> termRatings = findChildren(db, "Rating", termKey);

    for (Entity rating : termRatings) {
      dataList.add(Arrays.asList(rating.getProperty(property)));
    }
    return dataList;
  }

  private List<Entity> findChildren(DatastoreService db, String type, Key parent) {
    Query children = new Query(type).setAncestor(parent);
    List<Entity> result = db.prepare(children).asList(FetchOptions.Builder.withDefaults());
    return result;
  }

  private String makeJSON(Object changeItem) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      String jsonString = mapper.writeValueAsString(changeItem);
      return jsonString;
    } catch (Exception e) {
      return "Could not convert to JSON";
    }
  }
}
