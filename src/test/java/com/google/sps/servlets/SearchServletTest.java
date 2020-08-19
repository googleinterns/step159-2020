// // Copyright 2019 Google LLC
// //
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

// ignore this: needed so git will add the file to my pr

package com.google.sps.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.sps.data.Course;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** */
public final class SearchServletTest {

  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private SearchServlet searchObject;
  private AddSchoolData addSchool;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    searchObject = new SearchServlet();
    addSchool = new AddSchoolData();
  }

  @AfterEach
  public void tearDown() {
    helper.tearDown();
  }

  @Mock HttpServletRequest request;

  @Test
  /* Ensure the whole doGet process works with fuzzy search given all search filters are set. */
  public void GetCourses_SomeParamsSet_FuzzyMatch() throws IOException {

    request =
        createRequest(
            request,
            /* name */ "CS 105",
            /* professor */ "Smith",
            /* term */ "",
            /* units */ "2",
            /* school */ "stanford");
    List<Course> expectedCourses = new ArrayList<>();
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();

    addCourse("CS 104", "Smith", "Fall 2019", "1", "stanford", db);
    expectedCourses.add(
        new Course("CS 104", "Smith", Long.valueOf(1), "Fall 2019", "stanford", db));

    addCourse("CS 106", "Smith", "Spring 2020", "3", "stanford", db);
    expectedCourses.add(
        new Course("CS 106", "Smith", Long.valueOf(3), "Spring 2020", "stanford", db));

    addCourse("CS 105", "Smith", "Fall 2019", "3", "stanford", db);
    expectedCourses.add(
        new Course("CS 105", "Smith", Long.valueOf(3), "Fall 2019", "stanford", db));

    addCourse("CS 105", "Brown", "Fall 2019", "4", "stanford", db);

    addCourse("CS 103", "Smith", "Fall 2019", "3", "stanford", db);

    addCourse("CS 106", "Smith", "Fall 2019", "2", "stanford", db);
    expectedCourses.add(
        new Course("CS 106", "Smith", Long.valueOf(2), "Fall 2019", "stanford", db));

    Collections.sort(expectedCourses);

    JSONObject expected = new JSONObject();
    expected.put("courses", new Gson().toJson(expectedCourses));
    expected.put(
        "message",
        "We couldn't find anything exactly matching your query. Here are some similar results!");

    JSONObject json = searchObject.getMatchingCourses(request);

    assertEquals(json, expected);
  }

  @Test
  /* Ensure the whole doGet process works with fuzzy search given all search filters are set. */
  public void GetCourses_AllParamsSet_FuzzyMatch() throws IOException {

    request =
        createRequest(
            request,
            /* name */ "CS 105",
            /* professor */ "Smith",
            /* term */ "Spring 2020",
            /* units */ "2",
            /* school */ "stanford");
    List<Course> expectedCourses = new ArrayList<>();
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();

    addCourse("CS 104", "Smith", "Fall 2019", "1", "stanford", db);
    expectedCourses.add(
        new Course("CS 104", "Smith", Long.valueOf(1), "Fall 2019", "stanford", db));

    addCourse("CS 106", "Smith", "Spring 2020", "3", "stanford", db);
    expectedCourses.add(
        new Course("CS 106", "Smith", Long.valueOf(3), "Spring 2020", "stanford", db));

    addCourse("CS 105", "Smith", "Fall 2019", "3", "stanford", db);
    expectedCourses.add(
        new Course("CS 105", "Smith", Long.valueOf(3), "Fall 2019", "stanford", db));

    Collections.sort(expectedCourses);

    JSONObject expected = new JSONObject();
    expected.put("courses", new Gson().toJson(expectedCourses));
    expected.put(
        "message",
        "We couldn't find anything exactly matching your query. Here are some similar results!");

    JSONObject json = searchObject.getMatchingCourses(request);

    assertEquals(json, expected);
  }

  @Test
  /* Ensure the whole doGet process works as expected given no search filters. */
  public void GetCourses_NoParamsSet() {

    request =
        createRequest(
            request,
            /* name */ "",
            /* professor */ "",
            /* term */ "",
            /* units */ "",
            /* school */ "stanford");
    List<Course> expectedCourses = new ArrayList<>();
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();

    addCourse("CS 105", "Smith", "Spring 2020", "1", "stanford", db);
    expectedCourses.add(
        new Course("CS 105", "Smith", Long.valueOf(1), "Spring 2020", "stanford", db));

    addCourse("CS 107", "Smith", "Spring 2020", "2", "stanford", db);
    expectedCourses.add(
        new Course("CS 107", "Smith", Long.valueOf(2), "Spring 2020", "stanford", db));

    addCourse("CS 105", "Smith", "Spring 2020", "3", "stanford", db);
    expectedCourses.add(
        new Course("CS 105", "Smith", Long.valueOf(3), "Spring 2020", "stanford", db));

    Collections.sort(expectedCourses);

    JSONObject expected = new JSONObject();
    expected.put("courses", new Gson().toJson(expectedCourses));
    JSONObject json = searchObject.getMatchingCourses(request);

    assertEquals(json, expected);
  }

  @Test
  /* Ensure the whole doGet process works given all search filters are set. */
  public void GetCourses_AllParamsSet_ExactMatch() throws IOException {

    request =
        createRequest(
            request,
            /* name */ "CS 105",
            /* professor */ "Smith",
            /* term */ "Spring 2020",
            /* units */ "1,2,3"
            /* school */ ,
            "stanford");
    List<Course> expectedCourses = new ArrayList<>();
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();

    addCourse("CS 105", "Smith", "Spring 2020", "1", "stanford", db);
    expectedCourses.add(
        new Course("CS 105", "Smith", Long.valueOf(1), "Spring 2020", "stanford", db));

    addCourse("CS 105", "Smith", "Spring 2020", "2", "stanford", db);
    expectedCourses.add(
        new Course("CS 105", "Smith", Long.valueOf(2), "Spring 2020", "stanford", db));

    addCourse("CS 105", "Smith", "Spring 2020", "3", "stanford", db);
    expectedCourses.add(
        new Course("CS 105", "Smith", Long.valueOf(3), "Spring 2020", "stanford", db));

    Collections.sort(expectedCourses);

    JSONObject expected = new JSONObject();
    expected.put("courses", new Gson().toJson(expectedCourses));
    JSONObject json = searchObject.getMatchingCourses(request);
    assertEquals(json, expected);
  }

  private void addCourseEntity(
      String name, String professor, String term, int units, String school, DatastoreService db) {
    Entity entity = new Entity("Course-Info");
    entity.setProperty("name", name);
    entity.setProperty("professor", professor);
    entity.setProperty("units", units);
    entity.setProperty("term", term);
    entity.setProperty("school", school);
    db.put(entity);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("courseName")).thenReturn(name);
    when(request.getParameter("profName")).thenReturn(professor);
    when(request.getParameter("term")).thenReturn(term);
    when(request.getParameter("units")).thenReturn(String.valueOf(units));
    when(request.getParameter("school-name")).thenReturn(school);
    addSchool.addSchoolData(db, request);
  }

  private HttpServletRequest createRequest(
      HttpServletRequest request,
      String name,
      String professor,
      String term,
      String units,
      String school) {
    request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("courseName")).thenReturn(name);
    when(request.getParameter("profName")).thenReturn(professor);
    when(request.getParameter("term")).thenReturn(term);
    when(request.getParameter("units")).thenReturn(units);
    when(request.getParameter("school-name")).thenReturn(school);
    return request;
  }

  private void addCourse(
      String name, String prof, String term, String units, String school, DatastoreService db) {
    HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    when(req.getParameter("course-name")).thenReturn(name);
    when(req.getParameter("prof-name")).thenReturn(prof);
    when(req.getParameter("term")).thenReturn(term);
    when(req.getParameter("num-units")).thenReturn(units);
    when(req.getParameter("school-name")).thenReturn(school);
    SearchServlet.addCourse(req, db);
  }

  private void assertEqualsCourseArrays(List<Course> courses, List<Course> expectedCourses) {
    assertEquals(courses.size(), expectedCourses.size());
    for (int i = 0; i < courses.size(); i++) {
      Course actual = courses.get(i);
      Course expected = expectedCourses.get(i);
      assertEquals(actual.getName(), expected.getName());
      assertEquals(actual.getProfessor(), expected.getProfessor());
      assertEquals(actual.getUnits(), expected.getUnits());
      assertEquals(actual.getTerm(), expected.getTerm());
      assertEquals(actual.getSchool(), expected.getSchool());
    }
  }

  private List<Entity> findQueryMatch(
      DatastoreService db, String entityType, String entityProperty, String propertyValue) {
    Filter filter = new FilterPredicate(entityProperty, FilterOperator.EQUAL, propertyValue);
    Query q = new Query(entityType).setFilter(filter);
    return db.prepare(q).asList(FetchOptions.Builder.withDefaults());
  }
}
