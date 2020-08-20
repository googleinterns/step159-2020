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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.ProfDataHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public final class ProfessorDataTest {
  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private AddSchoolData schoolData;
  private ProfessorData professorData;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    schoolData = new AddSchoolData();
    professorData = new ProfessorData();
  }

  @AfterEach
  public void tearDown() {
    helper.tearDown();
  }

  @Mock HttpServletRequest request;
  @Mock HttpServletRequest requestB;
  @Mock HttpServletRequest requestC;

  @Test
  public void GettingRatingData_AllProperties() throws EntityNotFoundException {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();

    List<Object> expectedHoursListA = new ArrayList(Arrays.asList((long) 12));
    List<Object> expectedDifficultyListA = new ArrayList(Arrays.asList((long) 7));
    List<Object> expectedPerceptionListA = new ArrayList(Arrays.asList((double) 0.8));
    List<Object> expectedCommentsListA = new ArrayList(Arrays.asList("Great"));

    List<Object> expectedHoursListB = new ArrayList(Arrays.asList((long) 11));
    List<Object> expectedDifficultyListB = new ArrayList(Arrays.asList((long) 6));
    List<Object> expectedPerceptionListB = new ArrayList(Arrays.asList((double) 0.79));
    List<Object> expectedCommentsListB = new ArrayList(Arrays.asList("Good"));

    createRequest(
        /* requestServelt */ request,
        /* schoolName */ "MIT",
        /* courseName */ "6.006",
        /* termName */ "Spring 2020",
        /* units */ "12",
        /* profName */ "Jason Ku");
    createRequest(
        /* requestServelt */ requestB,
        /* schoolName */ "MIT",
        /* courseName */ "6.006",
        /* termName */ "Fall 2020",
        /* units */ "12",
        /* profName */ "Jason Ku");

    schoolData.addSchoolData(db, request);
    schoolData.addSchoolData(db, requestB);
    Key parent = findQueryMatch(db, "Term", "term", "Spring 2020").get(0).getKey();
    Key parent2 = findQueryMatch(db, "Term", "term", "Fall 2020").get(0).getKey();
    String profKeyStr = KeyFactory.keyToString((Key) db.get(parent).getProperty("professorKey"));

    addRatingEntity(
        /* database */ db,
        /* hours */ 12,
        /* difficulty */ 7,
        /* termScore */ 8,
        /* profScore */ 9,
        /* termPerception */ 0.82,
        /* professorPerception */ 0.8,
        /* termComment */ "Great",
        /* professorComment */ "Terrible",
        /* parentEntity */ parent);
    addRatingEntity(
        /* database */ db,
        /* hours */ 11,
        /* difficulty */ 6,
        /* termScore */ 7,
        /* profScore */ 8,
        /* termPerception */ 0.81,
        /* professorPerception */ 0.79,
        /* termComment */ "Good",
        /* professorComment */ "Bad",
        /* parentEntity */ parent2);

    createRequest(/* requestServelt */ requestC, /* prof-key */ profKeyStr);

    List<ProfDataHolder> answer = professorData.getAllDataFromProf(db, requestC);

    assertEquals(expectedHoursListA, answer.get(0).getHoursList());
    assertEquals(expectedDifficultyListA, answer.get(0).getDifficultyList());
    assertEquals(expectedPerceptionListA, answer.get(0).getPerceptionList());
    assertEquals(expectedCommentsListA, answer.get(0).getCommentsList());

    assertEquals(expectedHoursListB, answer.get(1).getHoursList());
    assertEquals(expectedDifficultyListB, answer.get(1).getDifficultyList());
    assertEquals(expectedPerceptionListB, answer.get(1).getPerceptionList());
    assertEquals(expectedCommentsListB, answer.get(1).getCommentsList());
  }

  private void createRequest(
      HttpServletRequest request,
      String schoolName,
      String courseName,
      String termName,
      String units,
      String profName) {
    when(request.getParameter("school-name")).thenReturn(schoolName);
    when(request.getParameter("course-name")).thenReturn(courseName);
    when(request.getParameter("term")).thenReturn(termName);
    when(request.getParameter("num-units")).thenReturn(units);
    when(request.getParameter("prof-name")).thenReturn(profName);
    when(request.getParameter("num-enrolled")).thenReturn("300");
  }

  private void createRequest(HttpServletRequest request, String profKeyStr) {
    when(request.getParameter("prof-key")).thenReturn(profKeyStr);
  }

  private List<Entity> findQueryMatch(
      DatastoreService db, String entityType, String entityProperty, String propertyValue) {
    Filter filter = new FilterPredicate(entityProperty, FilterOperator.EQUAL, propertyValue);
    Query q = new Query(entityType).setFilter(filter);
    List<Entity> result = db.prepare(q).asList(FetchOptions.Builder.withDefaults());
    return result;
  }

  private void addRatingEntity(
      DatastoreService db,
      int hours,
      int difficulty,
      int termScore,
      int profScore,
      double termPerception,
      double profPerception,
      String profComments,
      String termComments,
      Key parent) {
    Entity entity = new Entity("Rating", parent);
    entity.setProperty("hours", hours);
    entity.setProperty("difficulty", difficulty);
    entity.setProperty("score-term", termScore);
    entity.setProperty("score-professor", profScore);
    entity.setProperty("perception-term", termPerception);
    entity.setProperty("perception-professor", profPerception);
    entity.setProperty("comments-term", termComments);
    entity.setProperty("comments-professor", profComments);
    db.put(entity);
  }

  private Entity addTermEntity(DatastoreService db) {
    Entity entity = new Entity("Term");
    db.put(entity);
    return entity;
  }
}
