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

package com.google.sps.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    searchObject = new SearchServlet();
  }

  @AfterEach
  public void tearDown() {
    helper.tearDown();
  }

  @Mock HttpServletRequest request;

  @Test
  /* Ensure the correct filters are set when all search parameters are specified. */
  public void makeFiltersAllParams() {

    request = Mockito.mock(HttpServletRequest.class);

    when(request.getParameter("courseName")).thenReturn("CS 105");
    when(request.getParameter("profName")).thenReturn("Smith");
    when(request.getParameter("term")).thenReturn("Spring 2020");
    when(request.getParameter("units")).thenReturn("1,2,3");

    List<Filter> filters = searchObject.getFilters(request);

    List<Filter> expectedFilters = new ArrayList<>();
    List<Integer> units = new ArrayList<>();
    units.add(1);
    units.add(2);
    units.add(3);
    expectedFilters.add(new FilterPredicate("name", FilterOperator.EQUAL, "CS 105"));
    expectedFilters.add(new FilterPredicate("professor", FilterOperator.EQUAL, "Smith"));
    expectedFilters.add(new FilterPredicate("term", FilterOperator.EQUAL, "Spring 2020"));
    expectedFilters.add(new FilterPredicate("units", FilterOperator.IN, units));

    assertEquals(filters, expectedFilters);
  }

  @Test
  /* Ensure no filters are made given no search parameters are specified. */
  public void makeFiltersNoParams() {

    request = Mockito.mock(HttpServletRequest.class);

    // TODO: Find a better way to replicate null parameters -
    // getting NullPointerException when trying other methods.
    when(request.getParameter("courseName")).thenReturn("");
    when(request.getParameter("profName")).thenReturn("");
    when(request.getParameter("term")).thenReturn("select");
    when(request.getParameter("units")).thenReturn("");

    List<Filter> filters = searchObject.getFilters(request);

    List<Filter> expectedFilters = new ArrayList<>();

    assertEquals(filters, expectedFilters);
  }

  @Test
  /* Ensure the correct results are retrieved from Datastore given that all filters are set. */
  public void getResultsAllFilters() {

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);

    when(request.getParameter("courseName")).thenReturn("CS 105");
    when(request.getParameter("profName")).thenReturn("Smith");
    when(request.getParameter("term")).thenReturn("Spring 2020");
    when(request.getParameter("units")).thenReturn("1,2,3");

    List<Entity> expectedResults = new ArrayList<>();
    Entity entity1 = new Entity("Course");
    entity1.setProperty("name", "CS 105");
    entity1.setProperty("professor", "Smith");
    entity1.setProperty("term", "Spring 2020");
    entity1.setProperty("units", 1);
    db.put(entity1);
    expectedResults.add(entity1);

    Entity entity2 = new Entity("Course");
    entity2.setProperty("name", "CS 105");
    entity2.setProperty("professor", "Smith");
    entity2.setProperty("term", "Spring 2020");
    entity2.setProperty("units", 4);
    db.put(entity2);

    List<Filter> filters = searchObject.getFilters(request); // Tested in makeFiltersAllParams.
    List<Entity> results = searchObject.getResults(filters);

    assertEquals(results, expectedResults);
  }

  @Test
  /* Ensure the correct results are retrieved from Datastore given no search filters. */
  public void getResultsNoFilters() {

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);

    when(request.getParameter("courseName")).thenReturn("");
    when(request.getParameter("profName")).thenReturn("");
    when(request.getParameter("term")).thenReturn("select");
    when(request.getParameter("units")).thenReturn("");

    List<Entity> expectedResults = new ArrayList<>();
    Entity entity1 = new Entity("Course");
    entity1.setProperty("name", "CS 105");
    entity1.setProperty("professor", "Smith");
    entity1.setProperty("term", "Spring 2020");
    entity1.setProperty("units", 1);
    db.put(entity1);
    expectedResults.add(entity1);

    Entity entity2 = new Entity("Course");
    entity2.setProperty("name", "CS 105");
    entity2.setProperty("professor", "Smith");
    entity2.setProperty("term", "Spring 2020");
    entity2.setProperty("units", 4);
    db.put(entity2);
    expectedResults.add(entity2);

    List<Filter> filters = searchObject.getFilters(request); // Tested in makeFiltersNoParams.
    List<Entity> results = searchObject.getResults(filters);

    assertEquals(results, expectedResults);
  }

  @Test
  /* Ensure the whole doGet process works given all search filters are set. */
  public void getCoursesAllParams() {

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);

    when(request.getParameter("courseName")).thenReturn("CS 105");
    when(request.getParameter("profName")).thenReturn("Smith");
    when(request.getParameter("term")).thenReturn("Spring 2020");
    when(request.getParameter("units")).thenReturn("1,2,3");

    List<Course> expectedCourses = new ArrayList<>();

    Entity entity1 = new Entity("Course");
    entity1.setProperty("name", "CS 105");
    entity1.setProperty("professor", "Smith");
    entity1.setProperty("term", "Spring 2020");
    entity1.setProperty("units", 1);
    db.put(entity1);
    expectedCourses.add(new Course("CS 105", "Smith", Long.valueOf(1), "Spring 2020"));

    Entity entity2 = new Entity("Course");
    entity2.setProperty("name", "CS 105");
    entity2.setProperty("professor", "Smith");
    entity2.setProperty("term", "Spring 2020");
    entity2.setProperty("units", 2);
    db.put(entity2);
    expectedCourses.add(new Course("CS 105", "Smith", Long.valueOf(2), "Spring 2020"));

    Entity entity3 = new Entity("Course");
    entity3.setProperty("name", "CS 106");
    entity3.setProperty("professor", "Smith");
    entity3.setProperty("term", "Spring 2020");
    entity3.setProperty("units", 3);
    db.put(entity3);

    List<Filter> filters = searchObject.getFilters(request); // Tested in makeFiltersAllParams.
    List<Entity> results = searchObject.getResults(filters); // Tested in getResultsAllFilters.
    List<Course> courses = searchObject.getCourses(results);

    assertEquals(courses.size(), expectedCourses.size());
    for (int i = 0; i < courses.size(); i++) {
      Course actual = courses.get(i);
      Course expected = expectedCourses.get(i);
      assertEquals(actual.name, expected.name);
      assertEquals(actual.professor, expected.professor);
      assertEquals(actual.units, expected.units);
      assertEquals(actual.term, expected.term);
    }
  }

  @Test
  /* Ensure the whole doGet process works as expected given no search filters. */
  public void getCoursesNoParams() {

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);

    when(request.getParameter("courseName")).thenReturn("");
    when(request.getParameter("profName")).thenReturn("");
    when(request.getParameter("term")).thenReturn("select");
    when(request.getParameter("units")).thenReturn("");

    List<Course> expectedCourses = new ArrayList<>();

    Entity entity1 = new Entity("Course");
    entity1.setProperty("name", "CS 105");
    entity1.setProperty("professor", "Smith");
    entity1.setProperty("term", "Spring 2020");
    entity1.setProperty("units", 1);
    db.put(entity1);
    expectedCourses.add(new Course("CS 105", "Smith", Long.valueOf(1), "Spring 2020"));

    Entity entity2 = new Entity("Course");
    entity2.setProperty("name", "CS 105");
    entity2.setProperty("professor", "Smith");
    entity2.setProperty("term", "Spring 2020");
    entity2.setProperty("units", 2);
    db.put(entity2);
    expectedCourses.add(new Course("CS 105", "Smith", Long.valueOf(2), "Spring 2020"));

    Entity entity3 = new Entity("Course");
    entity3.setProperty("name", "CS 106");
    entity3.setProperty("professor", "Smith");
    entity3.setProperty("term", "Spring 2020");
    entity3.setProperty("units", 3);
    db.put(entity3);
    expectedCourses.add(new Course("CS 106", "Smith", Long.valueOf(3), "Spring 2020"));

    List<Filter> filters = searchObject.getFilters(request); // Tested in makeFiltersAllParams.
    List<Entity> results = searchObject.getResults(filters); // Tested in getResultsAllFilters.
    List<Course> courses = searchObject.getCourses(results);

    assertEquals(courses.size(), expectedCourses.size());
    for (int i = 0; i < courses.size(); i++) {
      Course actual = courses.get(i);
      Course expected = expectedCourses.get(i);
      assertEquals(actual.name, expected.name);
      assertEquals(actual.professor, expected.professor);
      assertEquals(actual.units, expected.units);
      assertEquals(actual.term, expected.term);
    }
  }
}
