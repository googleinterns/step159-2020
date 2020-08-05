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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
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

// TODO: ADD TEST FOR:
// - Missing/Malformed Data
// - Datastore put error
// - Identical Request
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
    request = createRequest(request, "MIT", "6.006", "Spring 2020", "12", "Jason Ku");

    newSchoolObject.addSchoolData(db, request);

    Entity schoolQuery =
        findQueryMatch(db, "School", "school-name", request.getParameter("school-name")).get(0);
    Entity expectedSchoolEntity = new Entity("School");
    expectedSchoolEntity.setProperty("school-name", "MIT");
    assertEquals(
        expectedSchoolEntity.getProperty("school-name"), schoolQuery.getProperty("school-name"));

    Entity courseQuery =
        findQueryMatch(db, "Course", "course-name", request.getParameter("course-name")).get(0);
    Entity expectedCourseEntity = new Entity("Course");
    expectedCourseEntity.setProperty("course-name", "6.006");
    assertEquals(
        expectedCourseEntity.getProperty("course-name"), courseQuery.getProperty("course-name"));

    Entity termQuery = findQueryMatch(db, "Term", "term", request.getParameter("term")).get(0);
    Entity expectedTermEntity = new Entity("Term");
    expectedTermEntity.setProperty("term", "Spring 2020");
    assertEquals(expectedTermEntity.getProperty("term"), termQuery.getProperty("term"));

    Entity profQuery =
        findQueryMatch(db, "Professor", "professor-name", request.getParameter("prof-name")).get(0);
    Entity expectedProfessorEntity = new Entity("Professor");
    expectedProfessorEntity.setProperty("professor-name", "Jason Ku");
    assertEquals(
        expectedProfessorEntity.getProperty("professor-name"),
        profQuery.getProperty("professor-name"));
  }

  @Test
  public void AddingNewTerm_NewSchool_NewCourse_NewProfessor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = createRequest(request, "MIT", "6.006", "Spring 2020", "12", "Jason Ku");
    requestB = createRequest(requestB, "Stanford", "CS 101", "Spring 2020", "12", "Jimmy");
    int exceptedTermResultLength = 2;
    int expectedSchoolResultLength = 2;
    int exceptedCourseResultLength = 2;
    int exceptedProfessorResultLength = 2;

    newSchoolObject.addSchoolData(db, request);
    newSchoolObject.addSchoolData(db, requestB);
    List<Entity> termQuery = findQueryMatch(db, "Term");
    List<Entity> schoolQuery = findQueryMatch(db, "School");
    List<Entity> courseQuery = findQueryMatch(db, "Course");
    List<Entity> profQuery = findQueryMatch(db, "Professor");

    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  @Test
  public void AddingNewCourseToOldSchool_CreatesNewEntityForAllExceptSchool() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = createRequest(request, "MIT", "6.006", "Spring 2020", "12", "Jason Ku");
    requestB = createRequest(request, "MIT", "6.08", "Spring 2020", "12", "Joe");
    int exceptedCourseResultLength = 2;
    int expectedSchoolResultLength = 1;
    int exceptedTermResultLength = 2;
    int exceptedProfessorResultLength = 2;

    newSchoolObject.addSchoolData(db, request);
    newSchoolObject.addSchoolData(db, requestB);
    List<Entity> termQuery = findQueryMatch(db, "Term");
    List<Entity> schoolQuery = findQueryMatch(db, "School");
    List<Entity> courseQuery = findQueryMatch(db, "Course");
    List<Entity> profQuery = findQueryMatch(db, "Professor");

    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  @Test
  public void AddingNewTerm_NoNewSchool_NewCourse_NoNewProfessor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = createRequest(request, "MIT", "6.006", "Spring 2020", "12", "Jason Ku");
    requestB = createRequest(request, "MIT", "6.08", "Spring 2020", "12", "Jason Ku");
    int exceptedTermResultLength = 2;
    int expectedSchoolResultLength = 1;
    int exceptedCourseResultLength = 2;
    int exceptedProfessorResultLength = 1;

    newSchoolObject.addSchoolData(db, request);
    newSchoolObject.addSchoolData(db, requestB);
    List<Entity> termQuery = findQueryMatch(db, "Term");
    List<Entity> schoolQuery = findQueryMatch(db, "School");
    List<Entity> courseQuery = findQueryMatch(db, "Course");
    List<Entity> profQuery = findQueryMatch(db, "Professor");

    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  @Test
  public void AddingNewTerm_NoNewSchool_NewCourse_NewProfessor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = createRequest(request, "MIT", "6.006", "Spring 2020", "12", "Jason Ku");
    requestB = createRequest(request, "MIT", "6.08", "Spring 2020", "12", "Srini");
    int exceptedTermResultLength = 2;
    int expectedSchoolResultLength = 1;
    int exceptedCourseResultLength = 2;
    int exceptedProfessorResultLength = 2;

    newSchoolObject.addSchoolData(db, request);
    newSchoolObject.addSchoolData(db, requestB);
    List<Entity> termQuery = findQueryMatch(db, "Term");
    List<Entity> schoolQuery = findQueryMatch(db, "School");
    List<Entity> courseQuery = findQueryMatch(db, "Course");
    List<Entity> profQuery = findQueryMatch(db, "Professor");

    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  @Test
  public void AddingNewTerm_NoNewSchool_NoNewCourse_NoNewProfessor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = createRequest(request, "MIT", "6.006", "Spring 2020", "12", "Jason Ku");
    requestB = createRequest(request, "MIT", "6.006", "Spring 2021", "12", "Jason Ku");
    int exceptedTermResultLength = 2;
    int expectedSchoolResultLength = 1;
    int exceptedCourseResultLength = 1;
    int exceptedProfessorResultLength = 1;

    newSchoolObject.addSchoolData(db, request);
    newSchoolObject.addSchoolData(db, requestB);
    List<Entity> termQuery = findQueryMatch(db, "Term");
    List<Entity> schoolQuery = findQueryMatch(db, "School");
    List<Entity> courseQuery = findQueryMatch(db, "Course");
    List<Entity> profQuery = findQueryMatch(db, "Professor");

    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  @Test
  public void AddingNewTerm_NoNewSchool_NoNewCourse_NewProfessor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = createRequest(request, "MIT", "6.006", "Spring 2020", "12", "Jason Ku");
    requestB = createRequest(request, "MIT", "6.006", "Spring 2021", "12", "Srini");
    int exceptedTermResultLength = 2;
    int expectedSchoolResultLength = 1;
    int exceptedCourseResultLength = 1;
    int exceptedProfessorResultLength = 2;

    newSchoolObject.addSchoolData(db, request);
    newSchoolObject.addSchoolData(db, requestB);
    List<Entity> termQuery = findQueryMatch(db, "Term");
    List<Entity> schoolQuery = findQueryMatch(db, "School");
    List<Entity> courseQuery = findQueryMatch(db, "Course");
    List<Entity> profQuery = findQueryMatch(db, "Professor");

    assertEquals(exceptedTermResultLength, termQuery.size());
    assertEquals(expectedSchoolResultLength, schoolQuery.size());
    assertEquals(exceptedCourseResultLength, courseQuery.size());
    assertEquals(exceptedProfessorResultLength, profQuery.size());
  }

  @Test
  public void AddingNewCourses_SameSchoolAncestor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = createRequest(request, "MIT", "6.006", "Spring 2020", "12", "Jason Ku");
    requestB = createRequest(request, "MIT", "6.08", "Spring 2021", "12", "Srini");

    newSchoolObject.addSchoolData(db, request);
    newSchoolObject.addSchoolData(db, requestB);
    Key school =
        findQueryMatch(db, "School", "school-name", request.getParameter("school-name"))
            .get(0)
            .getKey();
    List<Entity> children = findChildren(db, "Course", school);

    assertEquals(2, children.size());
  }

  @Test
  public void AddingNewTerms_SameCourseAncestor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = createRequest(request, "MIT", "6.006", "Spring 2020", "12", "Jason Ku");
    requestB = createRequest(request, "MIT", "6.006", "Spring 2021", "12", "Srini");

    newSchoolObject.addSchoolData(db, request);
    newSchoolObject.addSchoolData(db, requestB);
    Key course =
        findQueryMatch(db, "Course", "course-name", request.getParameter("course-name"))
            .get(0)
            .getKey();
    List<Entity> children = findChildren(db, "Term", course);

    assertEquals(2, children.size());
  }

  @Test
  public void AddingSchoolProfessors_SameSchoolAncestor() {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = createRequest(request, "MIT", "6.006", "Spring 2020", "12", "Jason Ku");
    requestB = createRequest(request, "MIT", "6.008", "Spring 2021", "12", "Srini");

    newSchoolObject.addSchoolData(db, request);
    newSchoolObject.addSchoolData(db, requestB);
    Key school =
        findQueryMatch(db, "School", "school-name", request.getParameter("school-name"))
            .get(0)
            .getKey();
    List<Entity> children = findChildren(db, "Professor", school);

    assertEquals(2, children.size());
  }

  private List<Entity> findQueryMatch(
      DatastoreService db, String entityType, String entityProperty, String propertyValue) {
    Filter filter = new FilterPredicate(entityProperty, FilterOperator.EQUAL, propertyValue);
    Query q = new Query(entityType).setFilter(filter);
    List<Entity> result = db.prepare(q).asList(FetchOptions.Builder.withDefaults());
    return result;
  }

  private List<Entity> findQueryMatch(DatastoreService db, String entityType) {
    Query q = new Query(entityType);
    List<Entity> result = db.prepare(q).asList(FetchOptions.Builder.withDefaults());
    return result;
  }

  private List<Entity> findChildren(DatastoreService db, String type, Key parent) {
    Query q = new Query(type).setAncestor(parent);
    List<Entity> result = db.prepare(q).asList(FetchOptions.Builder.withDefaults());
    return result;
  }

  private HttpServletRequest createRequest(
      HttpServletRequest request,
      String schoolName,
      String courseName,
      String termName,
      String units,
      String profName) {
    request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("school-name")).thenReturn(schoolName);
    when(request.getParameter("course-name")).thenReturn(courseName);
    when(request.getParameter("term")).thenReturn(termName);
    when(request.getParameter("num-units")).thenReturn(units);
    when(request.getParameter("prof-name")).thenReturn(profName);
    return request;
  }
}
