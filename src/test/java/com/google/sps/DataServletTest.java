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

  @Test
  public void AddingNewTermRating_NoTranslation() {

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);

    // First Term Rating for this term.
    when(request.getParameter("term-input")).thenReturn("I do not like this.");
    when(request.getParameter("rating-term")).thenReturn("1");
    when(request.getParameter("hoursOfWork")).thenReturn("8");
    when(request.getParameter("difficulty")).thenReturn("4");
    when(request.getParameter("prof-input")).thenReturn("The professor was amazing.");
    when(request.getParameter("rating-professor")).thenReturn("3");
    when(request.getParameter("languages")).thenReturn("false");
    when(request.getParameter("ID")).thenReturn("numberOneId");
    Entity termEntity = new Entity("Term");
    Key termKey = termEntity.getKey();
    when(request.getParameter("Course"))
        .thenReturn(Course("Spring 2020", "Smith", 12, termKey));

    newTermRating.addTermRating(request);

    Entity termRatingQuery =
        queryEntities("Rating", "reviewer-id", request.getParameter("reviewer-id")).get(0);
    Entity expectedTermRatingEntity = new Entity("Rating");
    expectedTermRatingEntity.setProperty("comments-term", "I do not like this.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-term"),
        termRatingQuery.getProperty("comments-term"));
    
    expectedTermRatingEntity.setProperty("reviewer-id", "numberOneId");
    assertEquals(
        expectedTermRatingEntity.getProperty("reviewer-id"),
        termRatingQuery.getProperty("reviewer-id"));
    
    expectedTermRatingEntity.setProperty("perception-term", Long.valueOf(1));
    assertEquals(
        expectedTermRatingEntity.getProperty("perception-term"),
        termRatingQuery.getProperty("perception-term"));
    
    expectedTermRatingEntity.setProperty("hours", Long.valueOf(8));
    assertEquals(
        expectedTermRatingEntity.getProperty("hours"), termRatingQuery.getProperty("hours"));
    
    expectedTermRatingEntity.setProperty("difficulty", Long.valueOf(4));
    assertEquals(
        expectedTermRatingEntity.getProperty("difficulty"),
        termRatingQuery.getProperty("difficulty"));
    
    expectedTermRatingEntity.setProperty("comments-professor", "The professor was amazing.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-professor"),
        termRatingQuery.getProperty("comments-professor"));
    
    expectedTermRatingEntity.setProperty("perception-professor", Long.valueOf(3));
    assertEquals(
        expectedTermRatingEntity.getProperty("perception-professor"),
        termRatingQuery.getProperty("perception-professor"));
    
    expectedTermRatingEntity.setProperty("score-term", -0.8999999761581421);
    assertEquals(
        expectedTermRatingEntity.getProperty("score-term"),
        termRatingQuery.getProperty("score-term"));
    
    expectedTermRatingEntity.setProperty("score-professor", 0.8999999761581421);
    assertEquals(
        expectedTermRatingEntity.getProperty("score-professor"),
        termRatingQuery.getProperty("score-professor"));
  }

  @Test
  public void OverwritingExistingTermRating_NoTranslation() {

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);

    when(request.getParameter("term-input")).thenReturn("I really like this class.");
    when(request.getParameter("rating-term")).thenReturn("4");
    when(request.getParameter("hoursOfWork")).thenReturn("8");
    when(request.getParameter("difficulty")).thenReturn("4");
    when(request.getParameter("prof-input")).thenReturn("The professor was okay.");
    when(request.getParameter("rating-professor")).thenReturn("3");
    when(request.getParameter("languages")).thenReturn("false");
    when(request.getParameter("ID")).thenReturn("numberOneId");
    Entity termEntity = new Entity("Term");
    Key termKey = termEntity.getKey();
    when(request.getParameter("Course"))
        .thenReturn(Course("Spring 2020", "Smith", 12, termKey));

    newTermRating.addTermRating(request);

    when(request.getParameter("term-input")).thenReturn("I don't like this class.");
    when(request.getParameter("rating-term")).thenReturn("1");
    when(request.getParameter("hoursOfWork")).thenReturn("10");
    when(request.getParameter("difficulty")).thenReturn("5");
    when(request.getParameter("prof-input")).thenReturn("This teacher was wonderful.");
    when(request.getParameter("rating-professor")).thenReturn("3");
    when(request.getParameter("languages")).thenReturn("false");
    when(request.getParameter("ID")).thenReturn("numberOneId");
    when(request.getParameter("Course"))
        .thenReturn(Course("Spring 2020", "Smith", 12, termKey));

    newTermRating.addTermRating(request);

    Entity termRatingQuery =
        queryEntities("Rating", "reviewer-id", request.getParameter("reviewer-id")).get(0);
    Entity expectedTermRatingEntity = new Entity("Rating");
    expectedTermRatingEntity.setProperty("comments-term", "I don't like this class.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-term"),
        termRatingQuery.getProperty("comments-term"));
    
    expectedTermRatingEntity.setProperty("reviewer-id", "numberOneId");
    assertEquals(
        expectedTermRatingEntity.getProperty("reviewer-id"),
        termRatingQuery.getProperty("reviewer-id"));
    
    expectedTermRatingEntity.setProperty("perception-term", Long.valueOf(1));
    assertEquals(
        expectedTermRatingEntity.getProperty("perception-term"),
        termRatingQuery.getProperty("perception-term"));
    
    expectedTermRatingEntity.setProperty("hours", Long.valueOf(10));
    assertEquals(
        expectedTermRatingEntity.getProperty("hours"), termRatingQuery.getProperty("hours"));
    
    expectedTermRatingEntity.setProperty("difficulty", Long.valueOf(5));
    assertEquals(
        expectedTermRatingEntity.getProperty("difficulty"),
        termRatingQuery.getProperty("difficulty"));
    
    expectedTermRatingEntity.setProperty("comments-professor", "This teacher was wonderful.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-professor"),
        termRatingQuery.getProperty("comments-professor"));
    
    expectedTermRatingEntity.setProperty("perception-professor", Long.valueOf(3));
    assertEquals(
        expectedTermRatingEntity.getProperty("perception-professor"),
        termRatingQuery.getProperty("perception-professor"));
    
    expectedTermRatingEntity.setProperty("score-term", -0.699999988079071);
    assertEquals(
        expectedTermRatingEntity.getProperty("score-term"),
        termRatingQuery.getProperty("score-term"));
    
    expectedTermRatingEntity.setProperty("score-professor", 0.800000011920929);
    assertEquals(
        expectedTermRatingEntity.getProperty("score-professor"),
        termRatingQuery.getProperty("score-professor"));
  }

  @Test
  public void AddingTermRating_WithTranslation() {

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);

    when(request.getParameter("term-input")).thenReturn("Esta clase es muy interesante.");
    when(request.getParameter("prof-input")).thenReturn("Este profesor es muy malo.");
    when(request.getParameter("languages")).thenReturn("true");
    when(request.getParameter("ID")).thenReturn("numberOneId");
    Entity termEntity = new Entity("Term");
    Key termKey = termEntity.getKey();
    when(request.getParameter("Course"))
        .thenReturn(Course("Spring 2020", "Smith", 12, termKey));

    newTermRating.addTermRating(request);

    Entity termRatingQuery =
        queryEntities("Rating", "reviewer-id", request.getParameter("reviewer-id")).get(0);
    Entity expectedTermRatingEntity = new Entity("Rating");
    expectedTermRatingEntity.setProperty("comments-term", "This class is very interesting.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-term"),
        termRatingQuery.getProperty("comments-term"));
    
    expectedTermRatingEntity.setProperty("comments-professor", "This teacher is very bad.");
    assertEquals(
        expectedTermRatingEntity.getProperty("comments-professor"),
        termRatingQuery.getProperty("comments-professor"));
  }
}
