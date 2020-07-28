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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that stores and shows images and comments. */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {

  @Override
  /* Show classes. */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<ClassObject> classes = new ArrayList<>();
    List<Filter> filters = new ArrayList<>();
    if (!request.getParameter("className").isEmpty()) {
      String className = request.getParameter("className");
      Filter classFilter = new FilterPredicate("Name", FilterOperator.EQUAL, className);
      filters.add(classFilter);
    }
    if (!request.getParameter("profName").isEmpty()) {
      String profName = request.getParameter("profName");
      Filter profFilter = new FilterPredicate("Professor", FilterOperator.EQUAL, profName);
      filters.add(profFilter);
    }
    List<Integer> units = new ArrayList<>();
    if (!request.getParameter("units").isEmpty()) {
      List<String> strUnits = Arrays.asList(request.getParameter("units").split(","));
      for (String number : strUnits) {
        units.add(Integer.valueOf(number));
      }
      if (!units.isEmpty()) {
        Filter unitsFilter = new FilterPredicate("Units", FilterOperator.IN, units);
        filters.add(unitsFilter);
      }
    }

    Query classQuery = new Query("Class");
    if (!filters.isEmpty()) {
      if (filters.size() == 1) {
        classQuery.setFilter(filters.get(0));
      } else {
        classQuery.setFilter(CompositeFilterOperator.and(filters));
      }
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results =
        datastore.prepare(classQuery).asList(FetchOptions.Builder.withDefaults());
    for (Entity entity : results) {
      ClassObject curr = new ClassObject();
      String name = (String) entity.getProperty("Name");
      curr.name = name;
      String professor = (String) entity.getProperty("Professor");
      curr.professor = professor;
      Long numUnits = (Long) entity.getProperty("Units");
      curr.units = numUnits;
      classes.add(curr);
    }
    String classesJson = new Gson().toJson(classes);
    response.setContentType("application/json;");
    response.getWriter().println(classesJson);
  }

  /* Store class. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("class-name");
    String prof = request.getParameter("prof-name");
    Integer units = Integer.parseInt(request.getParameter("num-units"));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity newClass = new Entity("Class");
    newClass.setProperty("Name", name);
    newClass.setProperty("Professor", prof);
    newClass.setProperty("Units", units);
    datastore.put(newClass);
    response.sendRedirect("/search.html");
  }
}
