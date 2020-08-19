// Copyright 2019 Google LLC
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
import com.google.appengine.api.datastore.Key;
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
import java.util.Collections;
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
  /* Show courses. */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JSONObject courseResults = getMatchingCourses(request);
    response.setContentType("application/json;");
    response.getWriter().println(courseResults);
  }

  enum MatchResult {
    BAD,
    OKAY,
    GOOD
  }

  /* Create list of courses given request. Public for testing purposes. */
  public JSONObject getMatchingCourses(HttpServletRequest request) {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    MatchResult matchResult = MatchResult.GOOD;
    List<Filter> filters = getFilters(request, /* isFuzzy = */ false);
    List<Entity> results = getResults(filters, db);
    if (results.isEmpty()) {
      filters = getFilters(request, /* isFuzzy = */ true);
      results = getResults(filters, db);
      matchResult = MatchResult.OKAY;
    }
    if (results.isEmpty()) { // Fuzzy search didn't work.
      matchResult = MatchResult.BAD;
    }
    return getCourses(results, matchResult, db);
  }

  /* Create list of filters given parameters specified in request. */
  private List<Filter> getFilters(HttpServletRequest request, boolean fuzzy) {
    List<Filter> filters = new ArrayList<>();
    if (!request.getParameter("courseName").isEmpty()) {
      String name = request.getParameter("courseName");
      Filter nameFilter;
      if (fuzzy) {
        String department = name.split(" ")[0];
        int courseNum = Integer.parseInt(name.split(" ")[1]);
        List<String> courseNums = new ArrayList<>();
        courseNums.add(department + " " + String.valueOf(courseNum + 1));
        courseNums.add(name);
        courseNums.add(department + " " + String.valueOf(courseNum - 1));
        nameFilter = new FilterPredicate("name", FilterOperator.IN, courseNums);
      } else {
        nameFilter = new FilterPredicate("name", FilterOperator.EQUAL, name);
      }
      filters.add(nameFilter);
    }

    if (!request.getParameter("profName").isEmpty()) {
      String profName = request.getParameter("profName");
      Filter profFilter = new FilterPredicate("professor", FilterOperator.EQUAL, profName);
      filters.add(profFilter);
    }

    if (!request.getParameter("term").isEmpty()) {
      String term = request.getParameter("term");
      Filter termFilter;
      if (fuzzy) {
        List<String> terms = new ArrayList<>();
        terms.add(getPrevTerm(term));
        terms.add(term);
        terms.add(getNextTerm(term));
        termFilter = new FilterPredicate("term", FilterOperator.IN, terms);
      } else {
        termFilter = new FilterPredicate("term", FilterOperator.EQUAL, term);
      }
      filters.add(termFilter);
    }

    if (!request.getParameter("units").isEmpty() && !fuzzy) {
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
  private List<Entity> getResults(List<Filter> filters, DatastoreService db) {
    Query courseQuery = new Query("Course-Info");
    if (!filters.isEmpty()) {
      if (filters.size() == 1) {
        courseQuery.setFilter(filters.get(0));
      } else {
        courseQuery.setFilter(CompositeFilterOperator.and(filters));
      }
    }
    List<Entity> results = db.prepare(courseQuery).asList(FetchOptions.Builder.withDefaults());
    return results;
  }

  /* Given list of result Entity courses, format into Course objects. */
  private JSONObject getCourses(
      List<Entity> results, MatchResult matchResult, DatastoreService db) {
    List<Course> courses = new ArrayList<>();
    for (Entity entity : results) {
      String name = (String) entity.getProperty("name");
      String professor = (String) entity.getProperty("professor");
      Long numUnits = (Long) entity.getProperty("units");
      String term = (String) entity.getProperty("term");
      String school = (String) entity.getProperty("school");
      Course course = new Course(name, professor, numUnits, term, school, db);
      courses.add(course);
    }

    Collections.sort(courses);
    JSONObject json = new JSONObject();

    switch (matchResult) {
      case BAD:
        json.put(
            "message",
            "We couldn't find anything relating to this query. Change your search parameters and try again.");
      case OKAY:
        json.put(
            "message",
            "We couldn't find anything exactly matching your query. Here are some similar results!");
        break;
    }
    String strCourses = new Gson().toJson(courses);
    json.put("courses", strCourses);
    return json;
  }

  /* Store course. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    addCourse(request, db);
    response.setStatus(200);
  }

  /* Public for testing. */
  public static void addCourse(HttpServletRequest request, DatastoreService db) {
    AddSchoolData addSchool = new AddSchoolData();
    addSchool.addSchoolData(db, request);

    String name = request.getParameter("course-name");
    String prof = request.getParameter("prof-name");
    Long units = Long.parseLong(request.getParameter("num-units"));
    String term = request.getParameter("term");
    String school = request.getParameter("school-name");

    Entity newCourse = new Entity("Course-Info");
    newCourse.setProperty("name", name);
    newCourse.setProperty("professor", prof);
    newCourse.setProperty("units", units);
    newCourse.setProperty("term", term);
    newCourse.setProperty("school", school);
    db.put(newCourse);
  }

  // TODO: Adapt this to quarter-system schools and potentially include summer quarter.
  private String getPrevTerm(String term) {
    String season = term.split(" ")[0];
    int year = Integer.parseInt(term.split(" ")[1]);
    if (season.equals("Fall")) {
      return "Spring " + String.valueOf(year);
    } else { // Season is "Spring".
      return "Fall " + String.valueOf(year - 1);
    }
  }

  private String getNextTerm(String term) {
    String season = term.split(" ")[0];
    int year = Integer.parseInt(term.split(" ")[1]);
    if (season.equals("Fall")) {
      return "Spring " + String.valueOf(year + 1);
    } else { // Season is "Spring"
      return "Fall " + String.valueOf(year);
    }
  }

  private Key findTermKey(
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

    return foundTerm.getKey();
  }

  private List<Entity> findQueryMatch(
      DatastoreService db, String entityType, String entityProperty, String propertyValue) {
    Filter filter = new FilterPredicate(entityProperty, FilterOperator.EQUAL, propertyValue);
    Query q = new Query(entityType).setFilter(filter);
    return db.prepare(q).asList(FetchOptions.Builder.withDefaults());
  }
}
