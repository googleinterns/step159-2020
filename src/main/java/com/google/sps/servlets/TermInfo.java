package com.google.sps.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/term-info")
public class TermInfo extends HttpServlet {
  private DatastoreService db = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      HashMap<String, String> termInfo = getTermInfo(db, request);
      String termInfoJSON = makeJSON(termInfo);
      response.setContentType("application/json;");
      response.getWriter().println(termInfoJSON);
    } catch (EntityNotFoundException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  public HashMap<String, String> getTermInfo(DatastoreService db, HttpServletRequest request)
      throws EntityNotFoundException {

    Key termKey = KeyFactory.stringToKey(request.getParameter("term-key"));
    Key courseKey = KeyFactory.stringToKey(request.getParameter("course-key"));

    String term = db.get(termKey).getProperty("term").toString();
    String enrolled = db.get(termKey).getProperty("num-enrolled").toString();
    String courseName = db.get(courseKey).getProperty("course-name").toString();
    String profKey = KeyFactory.keyToString((Key) db.get(termKey).getProperty("professorKey"));
    String profName =
        db.get((Key) db.get(termKey).getProperty("professorKey"))
            .getProperty("professor-name")
            .toString();

    HashMap<String, String> termData = new HashMap<String, String>();
    termData.put("course-name", courseName);
    termData.put("term-name", term);
    termData.put("num-enrolled", enrolled);
    termData.put("prof-name", profName);
    termData.put("prof-key", profKey);

    return termData;
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
