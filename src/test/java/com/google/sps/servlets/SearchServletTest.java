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
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.IOException;
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
  /* Ensure the whole doGet process works given all search filters are set. */
  public void GetCourses_AllParamsSet() throws IOException {

    request =
        createRequest(
            request, /*name*/
            "CS 105", /*professor*/
            "Smith", /*term*/
            "Spring 2020", /*units*/
            "1,2,3");
    List<Course> expectedCourses = new ArrayList<>();

    addCourseEntity("CS 105", "Smith", "Spring 2020", 1);
    expectedCourses.add(new Course("CS 105", "Smith", Long.valueOf(1), "Spring 2020"));

    addCourseEntity("CS 105", "Smith", "Spring 2020", 2);
    expectedCourses.add(new Course("CS 105", "Smith", Long.valueOf(2), "Spring 2020"));

    addCourseEntity("CS 106", "Smith", "Spring 2020", 3);

    List<Course> courses = searchObject.getHelper(request);
    System.out.println(response);

    assertEqualsCourseArrays(courses, expectedCourses);
  }

  @Test
  /* Ensure the whole doGet process works as expected given no search filters. */
  public void GetCourses_NoParamsSet() {

    request =
        createRequest(request, /*name*/ "", /*professor*/ "", /*term*/ "select", /*units*/ "");
    List<Course> expectedCourses = new ArrayList<>();

    addCourseEntity("CS 105", "Smith", "Spring 2020", 1);
    expectedCourses.add(new Course("CS 105", "Smith", Long.valueOf(1), "Spring 2020"));

    addCourseEntity("CS 105", "Smith", "Spring 2020", 2);
    expectedCourses.add(new Course("CS 105", "Smith", Long.valueOf(2), "Spring 2020"));

    addCourseEntity("CS 106", "Smith", "Spring 2020", 3);
    expectedCourses.add(new Course("CS 106", "Smith", Long.valueOf(3), "Spring 2020"));

    List<Course> courses = searchObject.getHelper(request);
    assertEqualsCourseArrays(courses, expectedCourses);
  }

  private void addCourseEntityWithList(
      String name, String professor, String term, int units, List<Entity> entities, boolean toAdd) {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("Course");
    entity.setProperty("name", name);
    entity.setProperty("professor", professor);
    entity.setProperty("units", units);
    entity.setProperty("term", term);
    db.put(entity);
    if (toAdd) {
      entities.add(entity);
    }
  }

  private void addCourseEntity(String name, String professor, String term, int units) {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("Course");
    entity.setProperty("name", name);
    entity.setProperty("professor", professor);
    entity.setProperty("units", units);
    entity.setProperty("term", term);
    db.put(entity);
  }

  private HttpServletRequest createRequest(
      HttpServletRequest request, String name, String professor, String term, String units) {
    request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("courseName")).thenReturn(name);
    when(request.getParameter("profName")).thenReturn(professor);
    when(request.getParameter("term")).thenReturn(term);
    when(request.getParameter("units")).thenReturn(units);
    return request;
  }

  private void assertEqualsCourseArrays(List<Course> courses, List<Course> expectedCourses) {
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
