package com.google.sps.servlets.second;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public final class DataServletTest {

  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private addTermRating newTermRating;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    newTermRating = new addTermRating();
  }

  @AfterEach
  public void tearDown() {
    helper.tearDown();
  }

  @Mock HttpServletRequest request;

  @Mock HttpServletRequest requestB;

  @Test
  public void AddingNewTermRatingNoSentimentAnalysisNoTranslation() {

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);

    // First Term Rating for this term.
    when(request.getParameter("class-input")).thenReturn("I really like this class.");
    when(request.getParameter("rating-class")).thenReturn("4");
    when(request.getParameter("hoursOfWork")).thenReturn("8");
    when(request.getParameter("difficulty")).thenReturn("4");
    when(request.getParameter("prof-input")).thenReturn("The professor was okay.");
    when(request.getParameter("rating-professor")).thenReturn("3");
    when(request.getParameter("languages")).thenReturn("false");
    when(request.getParameter("ID")).thenReturn("numberOneId");
    Entity termEntity = new Entity("Term");
    Key termKey = termEntity.getKey();
    when(request.getParameter("CourseClass"))
        .thenReturn(Course("Spring 2020", "Smith", 12, termKey));

    newTermRating.addTermRating(request);

    Entity termRatingQuery =
        queryEntities("Term Rating", reviewer - id, request.getParameter("reviewer-id")).get(0);
    Entity expectedTermRatingEntity = new Entity("Term Rating");
    expectedTermRatingEntity.setProperty("comments-class", "I really like this.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-class"),
        termRatingQuery.getProperty("comments-class"));
    expectedTermRatingEntity.setProperty("reviewer-id", "numberOneId");
    assertEquals(
        expectedTermRatingEntity.getProperty("reviewer-id"),
        termRatingQuery.getProperty("reviewer-id"));
    expectedTermRatingEntity.setProperty("perception-class", 4);
    assertEquals(
        expectedTermRatingEntity.getProperty("perception-class"),
        termRatingQuery.getProperty("perception-class"));
    expectedTermRatingEntity.setProperty("hours", 8);
    assertEquals(
        expectedTermRatingEntity.getProperty("hours"), termRatingQuery.getProperty("hours"));
    expectedTermRatingEntity.setProperty("difficulty", 4);
    assertEquals(
        expectedTermRatingEntity.getProperty("difficulty"),
        termRatingQuery.getProperty("difficulty"));
    expectedTermRatingEntity.setProperty("comments-professor", "The professor was okay.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-professor"),
        termRatingQuery.getProperty("comments-professor"));
    expectedTermRatingEntity.setProperty("perception-professor", 3);
    assertEquals(
        expectedTermRatingEntity.getProperty("perception-professor"),
        termRatingQuery.getProperty("perception-professor"));
  }

  @Test
  public void OverwritingExistingTermRatingNoSentimentAnalysisNoTranslation() {

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);

    when(request.getParameter("class-input")).thenReturn("I really like this class.");
    when(request.getParameter("rating-class")).thenReturn("4");
    when(request.getParameter("hoursOfWork")).thenReturn("8");
    when(request.getParameter("difficulty")).thenReturn("4");
    when(request.getParameter("prof-input")).thenReturn("The professor was okay.");
    when(request.getParameter("rating-professor")).thenReturn("3");
    when(request.getParameter("languages")).thenReturn("false");
    when(request.getParameter("ID")).thenReturn("numberOneId");
    Entity termEntity = new Entity("Term");
    Key termKey = termEntity.getKey();
    when(request.getParameter("CourseClass"))
        .thenReturn(Course("Spring 2020", "Smith", 12, termKey));

    newTermRating.addTermRating(request);

    when(request.getParameter("class-input")).thenReturn("I really hate this class.");
    when(request.getParameter("rating-class")).thenReturn("1");
    when(request.getParameter("hoursOfWork")).thenReturn("10");
    when(request.getParameter("difficulty")).thenReturn("5");
    when(request.getParameter("prof-input")).thenReturn("The professor was still okay.");
    when(request.getParameter("rating-professor")).thenReturn("3");
    when(request.getParameter("languages")).thenReturn("false");
    when(request.getParameter("ID")).thenReturn("numberOneId");
    when(request.getParameter("CourseClass"))
        .thenReturn(Course("Spring 2020", "Smith", 12, termKey));

    newTermRating.addTermRating(request);

    Entity termRatingQuery =
        queryEntities("Term Rating", reviewer - id, request.getParameter("reviewer-id")).get(0);
    Entity expectedTermRatingEntity = new Entity("Term Rating");
    expectedTermRatingEntity.setProperty("comments-class", "I really hate this class.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-class"),
        termRatingQuery.getProperty("comments-class"));
    expectedTermRatingEntity.setProperty("reviewer-id", "numberOneId");
    assertEquals(
        expectedTermRatingEntity.getProperty("reviewer-id"),
        termRatingQuery.getProperty("reviewer-id"));
    expectedTermRatingEntity.setProperty("perception-class", 1);
    assertEquals(
        expectedTermRatingEntity.getProperty("perception-class"),
        termRatingQuery.getProperty("perception-class"));
    expectedTermRatingEntity.setProperty("hours", 10);
    assertEquals(
        expectedTermRatingEntity.getProperty("hours"), termRatingQuery.getProperty("hours"));
    expectedTermRatingEntity.setProperty("difficulty", 5);
    assertEquals(
        expectedTermRatingEntity.getProperty("difficulty"),
        termRatingQuery.getProperty("difficulty"));
    expectedTermRatingEntity.setProperty("comments-professor", "The professor was still okay.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-professor"),
        termRatingQuery.getProperty("comments-professor"));
    expectedTermRatingEntity.setProperty("perception-professor", 3);
    assertEquals(
        expectedTermRatingEntity.getProperty("perception-professor"),
        termRatingQuery.getProperty("perception-professor"));
  }

  @Test
  public void AddingTermRatingWithSentimentAnalysis() {

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);

    when(request.getParameter("class-input")).thenReturn("I do not like this.");
    when(request.getParameter("rating-class")).thenReturn("4");
    when(request.getParameter("hoursOfWork")).thenReturn("8");
    when(request.getParameter("difficulty")).thenReturn("4");
    when(request.getParameter("prof-input")).thenReturn("The professor was amazing.");
    when(request.getParameter("rating-professor")).thenReturn("3");
    when(request.getParameter("languages")).thenReturn("false");
    when(request.getParameter("ID")).thenReturn("numberOneId");
    Entity termEntity = new Entity("Term");
    Key termKey = termEntity.getKey();
    when(request.getParameter("CourseClass"))
        .thenReturn(Course("Spring 2020", "Smith", 12, termKey));

    newTermRating.addTermRating(request);

    Entity termRatingQuery =
        queryEntities("Term Rating", reviewer - id, request.getParameter("reviewer-id")).get(0);
    Entity expectedTermRatingEntity = new Entity("Term Rating");
    expectedTermRatingEntity.setProperty("score-class", -0.8999999761581421);
    assertEquals(
        expectedTermRatingEntity.getProperty("score-class"),
        termRatingQuery.getProperty("score-class"));
    expectedTermRatingEntity.setProperty("score-professor", 0.8999999761581421);
    assertEquals(
        expectedTermRatingEntity.getProperty("score-professor"),
        termRatingQuery.getProperty("score-professor"));
  }

  @Test
  public void AddingTermRatingWithTranslation() {

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);

    when(request.getParameter("class-input")).thenReturn("Esta clase es muy interesante.");
    when(request.getParameter("rating-class")).thenReturn("4");
    when(request.getParameter("hoursOfWork")).thenReturn("8");
    when(request.getParameter("difficulty")).thenReturn("4");
    when(request.getParameter("prof-input")).thenReturn("Este profesor es muy malo.");
    when(request.getParameter("rating-professor")).thenReturn("3");
    when(request.getParameter("languages")).thenReturn("true");
    when(request.getParameter("ID")).thenReturn("numberOneId");
    Entity termEntity = new Entity("Term");
    Key termKey = termEntity.getKey();
    when(request.getParameter("CourseClass"))
        .thenReturn(Course("Spring 2020", "Smith", 12, termKey));

    newTermRating.addTermRating(request);

    Entity termRatingQuery =
        queryEntities("Term Rating", reviewer - id, request.getParameter("reviewer-id")).get(0);
    Entity expectedTermRatingEntity = new Entity("Term Rating");
    expectedTermRatingEntity.setProperty("comments-class", "This class is very interesting.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-class"),
        termRatingQuery.getProperty("comments-class"));
    expectedTermRatingEntity.setProperty("comments-professor", "This teacher is very bad.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-professor"),
        termRatingQuery.getProperty("comments-professor"));
  }
}
