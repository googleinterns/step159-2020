package com.google.sps.servlets;

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
import com.google.sps.data.TermDataHolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/term-live")
public class LiveCourseData extends HttpServlet {
  private DatastoreService db = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    getAllDataFromTerm(db, request);
    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.sendRedirect("/course.html");
  }

  public TermDataHolder getAllDataFromTerm(DatastoreService db, HttpServletRequest request) {
    TermDataHolder termDataHolder = new TermDataHolder();
    Entity foundTerm = getTerm(db, request);

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

  public Entity getTerm(DatastoreService db, HttpServletRequest request) {
    String schoolName = request.getParameter("school-name");
    String courseName = request.getParameter("course-name");
    String termName = request.getParameter("term");
    String profName = request.getParameter("prof-name");
    Long units = Long.parseLong(request.getParameter("num-units"));

    Entity foundTerm = findTerm(db, schoolName, courseName, termName, units, profName);
    return foundTerm;
  }

  private List<Object> getDataFromTermRating(DatastoreService db, Entity term, String property) {
    List<Object> dataList = new ArrayList();
    List<Entity> termRatings = findChildren(db, "Rating", term.getKey());

    for (Entity rating : termRatings) {
      dataList.add(rating.getProperty(property));
    }
    return dataList;
  }

  private Entity findTerm(
      DatastoreService db,
      String schoolName,
      String courseName,
      String termName,
      Long units,
      String profName) {
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

  private List<Entity> findChildren(DatastoreService db, String type, Key parent) {
    Query children = new Query(type).setAncestor(parent);
    List<Entity> result = db.prepare(children).asList(FetchOptions.Builder.withDefaults());
    return result;
  }
}
