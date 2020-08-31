package com.google.sps.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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

  // TODO: Rename Entity properties for perceptiona and score so they are not backwards
  public TermDataHolder getAllDataFromTerm(DatastoreService db, HttpServletRequest request) {
    TermDataHolder termDataHolder = new TermDataHolder();
    Key termKey = KeyFactory.stringToKey(request.getParameter("term-key"));

    termDataHolder.setHoursList(getDataFromTermRating(db, termKey, "hours"));
    termDataHolder.setDifficultyList(getDataFromTermRating(db, termKey, "difficulty"));
    termDataHolder.setTermScoreList(getDataFromTermRating(db, termKey, "perception-term"));
    termDataHolder.setTermPerceptionList(getDataFromTermRating(db, termKey, "score-term"));
    termDataHolder.setProfessorPerceptionList(
        getDataFromTermRating(db, termKey, "score-professor"));
    termDataHolder.setProfessorScoreList(
        getDataFromTermRating(db, termKey, "perception-professor"));
    termDataHolder.setTermCommentsList(getDataFromTermRating(db, termKey, "comments-term"));
    termDataHolder.setProfessorCommentsList(
        getDataFromTermRating(db, termKey, "comments-professor"));
    termDataHolder.setGradesList(getDataFromTermRating(db, termKey, "grade"));
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
