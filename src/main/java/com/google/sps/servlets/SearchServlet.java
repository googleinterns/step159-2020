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
import com.google.sps.data.Course;
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
  /* Show courses. */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Course> courses = getHelper(request);

    String coursesJson = new Gson().toJson(courses);
    response.setContentType("application/json;");
    response.getWriter().println(coursesJson);
  }

  /* Create list of courses given request. Used for testing. */
  public List<Course> getHelper(HttpServletRequest request) {
    List<Filter> filters = getFilters(request);
    List<Entity> results = getResults(filters);
    List<Course> courses = getCourses(results);
    return courses;
  }

  /* Create list of filters given parameters specified in request. */
  private List<Filter> getFilters(HttpServletRequest request) {
    List<Filter> filters = new ArrayList<>();
    if (!request.getParameter("courseName").isEmpty()) {
      String name = request.getParameter("courseName");
      Filter nameFilter = new FilterPredicate("name", FilterOperator.EQUAL, name);
      filters.add(nameFilter);
    }

    if (!request.getParameter("profName").isEmpty()) {
      String profName = request.getParameter("profName");
      Filter profFilter = new FilterPredicate("professor", FilterOperator.EQUAL, profName);
      filters.add(profFilter);
    }

    if (!request.getParameter("term").isEmpty()) {
      String term = request.getParameter("term");
      Filter termFilter = new FilterPredicate("term", FilterOperator.EQUAL, term);
      filters.add(termFilter);
    }

    if (!request.getParameter("units").isEmpty()) {
      List<Integer> units = new ArrayList<>();
      List<String> strUnits = Arrays.asList(request.getParameter("units").split(","));
      for (String number : strUnits) {
        units.add(Integer.valueOf(number));
      }
      if (!units.isEmpty()) {
        Filter unitsFilter = new FilterPredicate("units", FilterOperator.IN, units);
        filters.add(unitsFilter);
      }
    }
    String school = request.getParameter("school-name");
    Filter schoolFilter = new FilterPredicate("school", FilterOperator.EQUAL, school);
    return filters;
  }

  /* Combine filters, if applicable, and get results from Datastore matching this combination. */
  private List<Entity> getResults(List<Filter> filters) {
    Query courseQuery = new Query("Course-Info");
    if (!filters.isEmpty()) {
      if (filters.size() == 1) {
        courseQuery.setFilter(filters.get(0));
      } else {
        courseQuery.setFilter(CompositeFilterOperator.and(filters));
      }
    }
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results =
        datastore.prepare(courseQuery).asList(FetchOptions.Builder.withDefaults());
    return results;
  }

  /* Given list of result Entity courses, format into Course objects. */
  private List<Course> getCourses(List<Entity> results) {
    List<Course> courses = new ArrayList<>();
    for (Entity entity : results) {
      String name = (String) entity.getProperty("name");
      String professor = (String) entity.getProperty("professor");
      Long numUnits = (Long) entity.getProperty("units");
      String term = (String) entity.getProperty("term");
      String school = (String) entity.getProperty("school");
      Course course = new Course(name, professor, numUnits, term, school);
      courses.add(course);
    }
    return courses;
  }

  /* Store course. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("course-name");
    String prof = request.getParameter("prof-name");
    Long units = Long.parseLong(request.getParameter("num-units"));
    String term = request.getParameter("term");
    String school = request.getParameter("school-name");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    AddSchoolData addSchool = new AddSchoolData();
    addSchool.addSchoolData(datastore, request);

    Entity newCourse = new Entity("Course-Info");
    newCourse.setProperty("name", name);
    newCourse.setProperty("professor", prof);
    newCourse.setProperty("units", units);
    newCourse.setProperty("term", term);
    newCourse.setProperty("school", school);
    datastore.put(newCourse);
    response.sendRedirect("/index.html");
  }
}
