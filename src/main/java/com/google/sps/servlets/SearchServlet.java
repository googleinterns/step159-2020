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
import org.json.simple.JSONObject;

/** Servlet that stores and shows images and comments. */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {

  @Override
  /* Show classes. */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<String> classNames = new ArrayList<>();
    List<String> professorNames = new ArrayList<>();
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
      if (units.size() != 0) {
        Filter unitsFilter = new FilterPredicate("Units", FilterOperator.IN, units);
        filters.add(unitsFilter);
      }
    }

    Query q = new Query("Class");
    // Add filters depending on number of fields specified
    if (filters.size() == 1) {
      q.setFilter(filters.get(0));
    } else if (filters.size() == 2) {
      q.setFilter(CompositeFilterOperator.and(filters.get(0), filters.get(1)));
    } else if (filters.size() == 3) {
      q.setFilter(
          CompositeFilterOperator.and(
              CompositeFilterOperator.and(filters.get(0), filters.get(1), filters.get(2))));
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> classes = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
    for (Entity entity : classes) {
      String name = (String) entity.getProperty("Name");
      classNames.add(name);
      String professor = (String) entity.getProperty("Professor");
      professorNames.add(professor);
    }

    // Generate links for class pages and send them back - set variables in URL
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
    Integer units = Integer.valueOf(request.getParameter("num-units"));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity newClass = new Entity("Class");
    newClass.setProperty("Name", name);
    newClass.setProperty("Professor", prof);
    newClass.setProperty("Units", units);
    datastore.put(newClass);
    response.sendRedirect("/search.html");
  }
}
