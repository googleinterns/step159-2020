package com.google.sps.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/report")
public class Report extends HttpServlet {
  private DatastoreService db = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity newBug = new Entity("Report");
    newBug.setProperty("type", request.getParameter("report-type"));
    newBug.setProperty("description", request.getParameter("description"));
    newBug.setProperty("user-email", request.getParameter("user-email"));
    db.put(newBug);
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
