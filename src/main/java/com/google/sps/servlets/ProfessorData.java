package com.google.sps.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.sps.data.ProfDataHolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/prof-data")
public class ProfessorData extends HttpServlet {
  private DatastoreService db = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      List<ProfDataHolder> profPageData = getAllDataFromProf(db, request);
      String profPageDataJSON = makeJSON(profPageData);
      response.setContentType("application/json;");
      response.getWriter().println(profPageDataJSON);
    } catch (EntityNotFoundException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  public List<ProfDataHolder> getAllDataFromProf(DatastoreService db, HttpServletRequest request)
      throws EntityNotFoundException {
    List<ProfDataHolder> profData = new ArrayList();
    Key profKey = KeyFactory.stringToKey(request.getParameter("prof-key"));

    Filter profFilter = new FilterPredicate("professorKey", FilterOperator.EQUAL, profKey);
    Query profTermQuery = new Query("Term").setFilter(profFilter);
    List<Entity> foundTerms = db.prepare(profTermQuery).asList(FetchOptions.Builder.withDefaults());

    for (Entity term : foundTerms) {
      List<Entity> termRatings = findChildren(db, "Rating", term.getKey());
      ProfDataHolder profDataHolderElement = new ProfDataHolder();
      profDataHolderElement.setTerm(term.getProperty("term").toString());
      profDataHolderElement.setCourse(
          db.get(term.getParent()).getProperty("course-name").toString());
      profDataHolderElement.setCourseKey(KeyFactory.keyToString(db.get(term.getParent()).getKey()));
      profDataHolderElement.setTermKey(KeyFactory.keyToString(term.getKey()));

      List<Object> hoursList = new ArrayList();
      List<Object> difficultyList = new ArrayList();
      List<Object> perceptionList = new ArrayList();
      List<Object> commentsList = new ArrayList();

      for (Entity rating : termRatings) {
        hoursList.add(rating.getProperty("hours"));
        difficultyList.add(rating.getProperty("difficulty"));
        perceptionList.add(rating.getProperty("perception-professor"));
        commentsList.add(rating.getProperty("comments-professor"));
      }

      profDataHolderElement.setHoursList(hoursList);
      profDataHolderElement.setDifficultyList(difficultyList);
      profDataHolderElement.setPerceptionList(perceptionList);
      profDataHolderElement.setCommentsList(commentsList);
      profData.add(profDataHolderElement);
    }
    return profData;
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
