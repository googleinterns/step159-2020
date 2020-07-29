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

package com.google.sps.servlets.second;

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
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** */
public final class AddSchoolDataTest {

  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private AddSchoolData newSchoolObject;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    newSchoolObject = new AddSchoolData();
  }

  @AfterEach
  public void tearDown() {
    helper.tearDown();
  }

  @Mock HttpServletRequest request;

  @Mock HttpServletRequest requestB;

  @Test
  public void AddingNewSchool_CreatesNewEntityForAll() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("school-name")).thenReturn("MIT");
    when(request.getParameter("course-name")).thenReturn("6.006");
    when(request.getParameter("term")).thenReturn("Spring 2020");
    when(request.getParameter("units")).thenReturn("12");
    when(request.getParameter("professor-name")).thenReturn("Jason Ku");
    newSchoolObject.addSchoolData(db, request);

    Entity schoolQuery =
        findQueryMatch(db, "School", "school-name", request.getParameter("school-name"), false)
            .get(0);
    Entity expectedSchoolEntity = new Entity("School");
    expectedSchoolEntity.setProperty("school-name", "MIT");
    assertEquals(
        expectedSchoolEntity.getProperty("school-name"), schoolQuery.getProperty("school-name"));

    Entity courseQuery =
        findQueryMatch(db, "Course", "course-name", request.getParameter("course-name"), false)
            .get(0);
    Entity expectedCourseEntity = new Entity("Course");
    expectedCourseEntity.setProperty("course-name", "6.006");
    assertEquals(
        expectedCourseEntity.getProperty("course-name"), courseQuery.getProperty("course-name"));

    Entity termQuery =
        findQueryMatch(db, "Term", "term", request.getParameter("term"), false).get(0);
    Entity expectedTermEntity = new Entity("Term");
    expectedTermEntity.setProperty("term", "Spring 2020");
    assertEquals(expectedTermEntity.getProperty("term"), termQuery.getProperty("term"));

    Entity profQuery =
        findQueryMatch(
                db, "Professor", "professor-name", request.getParameter("professor-name"), false)
            .get(0);
    Entity expectedProfessorEntity = new Entity("Professor");
    expectedProfessorEntity.setProperty("professor-name", "Jason Ku");
    assertEquals(
        expectedProfessorEntity.getProperty("professor-name"),
        profQuery.getProperty("professor-name"));
  }

  @Test
  public void AddingNewTerm_NewSchool_NewCourse_NewProfessor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("school-name")).thenReturn("MIT");
    when(request.getParameter("course-name")).thenReturn("6.006");
    when(request.getParameter("term")).thenReturn("Spring 2020");
    when(request.getParameter("units")).thenReturn("12");
    when(request.getParameter("professor-name")).thenReturn("Jason Ku");
    newSchoolObject.addSchoolData(db, request);

    requestB = Mockito.mock(HttpServletRequest.class);
    when(requestB.getParameter("school-name")).thenReturn("Stanford");
    when(requestB.getParameter("course-name")).thenReturn("CS 101");
    when(requestB.getParameter("term")).thenReturn("Spring 2020");
    when(requestB.getParameter("units")).thenReturn("12");
    when(requestB.getParameter("professor-name")).thenReturn("Jimmy");
    newSchoolObject.addSchoolData(db, requestB);

    List<Entity> schoolQuery =
        findQueryMatch(db, "School", "school-name", request.getParameter("school-name"), true);
    List<Entity> courseQuery =
        findQueryMatch(db, "Course", "course-name", request.getParameter("course-name"), true);
    List<Entity> termQuery = findQueryMatch(db, "Term", "term", request.getParameter("term"), true);
    List<Entity> profQuery =
        findQueryMatch(
            db, "Professor", "professor-name", request.getParameter("professor-name"), true);

    int expectedSchoolResultLength = 2;
    int exceptedCourseResultLength = 2;
    int exceptedTermResultLength = 2;
    int exceptedProfessorResultLength = 2;

    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  @Test
  public void AddingNewCourseToOldSchool_CreatesNewEntityForAllExceptSchool() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("school-name")).thenReturn("MIT");
    when(request.getParameter("course-name")).thenReturn("6.006");
    when(request.getParameter("term")).thenReturn("Spring 2020");
    when(request.getParameter("units")).thenReturn("12");
    when(request.getParameter("professor-name")).thenReturn("Jason Ku");
    newSchoolObject.addSchoolData(db, request);

    requestB = Mockito.mock(HttpServletRequest.class);
    when(requestB.getParameter("school-name")).thenReturn("MIT");
    when(requestB.getParameter("course-name")).thenReturn("6.08");
    when(requestB.getParameter("term")).thenReturn("Spring 2020");
    when(requestB.getParameter("units")).thenReturn("12");
    when(requestB.getParameter("professor-name")).thenReturn("Joe");
    newSchoolObject.addSchoolData(db, requestB);

    System.out.println(requestB);

    List<Entity> schoolQuery =
        findQueryMatch(db, "School", "school-name", request.getParameter("school-name"), true);
    List<Entity> courseQuery =
        findQueryMatch(db, "Course", "course-name", request.getParameter("course-name"), true);
    List<Entity> termQuery = findQueryMatch(db, "Term", "term", request.getParameter("term"), true);
    List<Entity> profQuery =
        findQueryMatch(
            db, "Professor", "Professor-name", request.getParameter("professor-name"), true);

    int expectedSchoolResultLength = 1;
    int exceptedCourseResultLength = 2;
    int exceptedTermResultLength = 2;
    int exceptedProfessorResultLength = 2;

    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  @Test
  public void AddingNewTerm_NoNewSchool_NewCourse_NoNewProfessor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("school-name")).thenReturn("MIT");
    when(request.getParameter("course-name")).thenReturn("6.006");
    when(request.getParameter("term")).thenReturn("Spring 2020");
    when(request.getParameter("units")).thenReturn("12");
    when(request.getParameter("professor-name")).thenReturn("Jason Ku");
    newSchoolObject.addSchoolData(db, request);

    requestB = Mockito.mock(HttpServletRequest.class);
    when(requestB.getParameter("school-name")).thenReturn("MIT");
    when(requestB.getParameter("course-name")).thenReturn("6.08");
    when(requestB.getParameter("term")).thenReturn("Spring 2020");
    when(requestB.getParameter("units")).thenReturn("12");
    when(requestB.getParameter("professor-name")).thenReturn("Jason Ku");
    newSchoolObject.addSchoolData(db, requestB);

    List<Entity> schoolQuery =
        findQueryMatch(db, "School", "school-name", request.getParameter("school-name"), true);
    List<Entity> courseQuery =
        findQueryMatch(db, "Course", "course-name", request.getParameter("course-name"), true);
    List<Entity> termQuery = findQueryMatch(db, "Term", "term", request.getParameter("term"), true);
    List<Entity> profQuery =
        findQueryMatch(
            db, "Professor", "professor-name", request.getParameter("professor-name"), true);

    int expectedSchoolResultLength = 1;
    int exceptedCourseResultLength = 2;
    int exceptedTermResultLength = 2;
    int exceptedProfessorResultLength = 1;

    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  @Test
  public void AddingNewTerm_NoNewSchool_NewCourse_NewProfessor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("school-name")).thenReturn("MIT");
    when(request.getParameter("course-name")).thenReturn("6.006");
    when(request.getParameter("term")).thenReturn("Spring 2020");
    when(request.getParameter("units")).thenReturn("12");
    when(request.getParameter("professor-name")).thenReturn("Jason Ku");
    newSchoolObject.addSchoolData(db, request);

    requestB = Mockito.mock(HttpServletRequest.class);
    when(requestB.getParameter("school-name")).thenReturn("MIT");
    when(requestB.getParameter("course-name")).thenReturn("6.08");
    when(requestB.getParameter("term")).thenReturn("Spring 2020");
    when(requestB.getParameter("units")).thenReturn("12");
    when(requestB.getParameter("professor-name")).thenReturn("Srini");
    newSchoolObject.addSchoolData(db, requestB);

    List<Entity> schoolQuery =
        findQueryMatch(db, "School", "school-name", request.getParameter("school-name"), true);
    List<Entity> courseQuery =
        findQueryMatch(db, "Course", "course-name", request.getParameter("course-name"), true);
    List<Entity> termQuery = findQueryMatch(db, "Term", "term", request.getParameter("term"), true);
    List<Entity> profQuery =
        findQueryMatch(
            db, "Professor", "professor-name", request.getParameter("professor-name"), true);

    int expectedSchoolResultLength = 1;
    int exceptedCourseResultLength = 2;
    int exceptedTermResultLength = 2;
    int exceptedProfessorResultLength = 2;

    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  @Test
  public void AddingNewTerm_NoNewSchool_NoNewCourse_NoNewProfessor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("school-name")).thenReturn("MIT");
    when(request.getParameter("course-name")).thenReturn("6.006");
    when(request.getParameter("term")).thenReturn("Spring 2020");
    when(request.getParameter("units")).thenReturn("12");
    when(request.getParameter("professor-name")).thenReturn("Jason Ku");
    newSchoolObject.addSchoolData(db, request);

    requestB = Mockito.mock(HttpServletRequest.class);
    when(requestB.getParameter("school-name")).thenReturn("MIT");
    when(requestB.getParameter("course-name")).thenReturn("6.006");
    when(requestB.getParameter("term")).thenReturn("Spring 2021");
    when(requestB.getParameter("units")).thenReturn("12");
    when(requestB.getParameter("professor-name")).thenReturn("Jason Ku");
    newSchoolObject.addSchoolData(db, requestB);

    List<Entity> schoolQuery =
        findQueryMatch(db, "School", "school-name", request.getParameter("school-name"), true);
    List<Entity> courseQuery =
        findQueryMatch(db, "Course", "course-name", request.getParameter("course-name"), true);
    List<Entity> termQuery = findQueryMatch(db, "Term", "term", request.getParameter("term"), true);
    List<Entity> profQuery =
        findQueryMatch(
            db, "Professor", "professor-name", request.getParameter("professor-name"), true);

    int expectedSchoolResultLength = 1;
    int exceptedCourseResultLength = 1;
    int exceptedTermResultLength = 2;
    int exceptedProfessorResultLength = 1;

    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  @Test
  public void AddingNewTerm_NoNewSchool_NoNewCourse_NewProfessor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("school-name")).thenReturn("MIT");
    when(request.getParameter("course-name")).thenReturn("6.006");
    when(request.getParameter("term")).thenReturn("Spring 2020");
    when(request.getParameter("units")).thenReturn("12");
    when(request.getParameter("professor-name")).thenReturn("Jason Ku");
    newSchoolObject.addSchoolData(db, request);

    requestB = Mockito.mock(HttpServletRequest.class);
    when(requestB.getParameter("school-name")).thenReturn("MIT");
    when(requestB.getParameter("course-name")).thenReturn("6.006");
    when(requestB.getParameter("term")).thenReturn("Spring 2021");
    when(requestB.getParameter("units")).thenReturn("12");
    when(requestB.getParameter("professor-name")).thenReturn("Srini");
    newSchoolObject.addSchoolData(db, requestB);

    List<Entity> schoolQuery =
        findQueryMatch(db, "School", "school-name", request.getParameter("school-name"), true);
    List<Entity> courseQuery =
        findQueryMatch(db, "Course", "course-name", request.getParameter("course-name"), true);
    List<Entity> termQuery = findQueryMatch(db, "Term", "term", request.getParameter("term"), true);
    List<Entity> profQuery =
        findQueryMatch(
            db, "Professor", "professor-name", request.getParameter("professor-name"), true);

    int expectedSchoolResultLength = 1;
    int exceptedCourseResultLength = 1;
    int exceptedTermResultLength = 2;
    int exceptedProfessorResultLength = 2;

    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  public List<Entity> findQueryMatch(
      DatastoreService db,
      String entityType,
      String entityProperty,
      String propertyValue,
      Boolean onlyType) {
    Query q;

    if (onlyType) {
      q = new Query(entityType);
    } else {
      Filter filter = new FilterPredicate(entityProperty, FilterOperator.EQUAL, propertyValue);
      q = new Query(entityType).setFilter(filter);
    }
    List<Entity> result = db.prepare(q).asList(FetchOptions.Builder.withDefaults());
    return result;
  }
}
