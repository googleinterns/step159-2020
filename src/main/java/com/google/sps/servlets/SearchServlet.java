// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/** Servlet that stores and shows images and comments. */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {

  @Override
  /* Show classes. */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<String> classNames = new ArrayList<>();
    List<String> professorNames = new ArrayList<>();
    String profName = request.getParameter("profName");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Filter propertyFilter = new FilterPredicate("Professor", FilterOperator.EQUAL, profName);
    Query q = new Query("Class").setFilter(propertyFilter);
    List<Entity> classes = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
    System.out.println(classes);
    for (Entity entity : classes) {
      String name = (String) entity.getProperty("Name");
      classNames.add(name);
      String professor = (String) entity.getProperty("Professor");
      professorNames.add(professor);
    }

    System.out.println(classNames);
    String classNameJson = new Gson().toJson(classNames);
    String profNameJson = new Gson().toJson(professorNames);

    JSONObject obj = new JSONObject();
    obj.put("classNames", classNameJson);
    obj.put("profNames", profNameJson);
    response.setContentType("application/json;");
    response.getWriter().println(obj);
  }

  /* Store class. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("class-name");
    String prof = request.getParameter("prof-name");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity newClass = new Entity("Class");
    newClass.setProperty("Name", name);
    newClass.setProperty("Professor", prof);
    datastore.put(newClass);
    response.sendRedirect("/search.html");
  }
}
