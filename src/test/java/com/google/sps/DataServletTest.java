package com.google.sps.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class DataServletTest {

  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private DataServlet newTermRating;
  private Entity termEntity;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    languageService = Mockito.mock(LanguageServiceClient.class);
    newTermRating = new DataServlet(languageService);
  }

  @AfterEach
  public void tearDown() {
    helper.tearDown();
  }

  @Mock HttpServletRequest request;
  @Mock LanguageServiceClient languageService;

  @Test
  public void addTermRating_newRating() throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    request = Mockito.mock(HttpServletRequest.class);
    Entity termEntity = new Entity("Term");
    datastore.put(termEntity);
    Key termKey = termEntity.getKey();

    when(request.getParameter("term")).thenReturn(KeyFactory.keyToString(termKey));
    when(request.getParameter("term-input")).thenReturn("I do not like this.");
    when(request.getParameter("rating-term")).thenReturn("1");
    when(request.getParameter("hoursOfWork")).thenReturn("8");
    when(request.getParameter("difficulty")).thenReturn("4");
    when(request.getParameter("prof-input")).thenReturn("The professor was amazing.");
    when(request.getParameter("rating-professor")).thenReturn("3");
    when(request.getParameter("ID")).thenReturn("numberOneId");

    AnalyzeSentimentResponse response =
        AnalyzeSentimentResponse.newBuilder()
            .setDocumentSentiment(Sentiment.newBuilder().setScore((float) -0.8999999761581421))
            .build();
    when(languageService.analyzeSentiment(any(Document.class))).thenReturn(response);

    newTermRating.addTermRating(request);

    Entity termRatingEntity =
        newTermRating
            .queryEntities(
                /* entityName */ "Rating",
                /* propertyName */ "reviewer-id",
                /* propertyValue */ "numberOneId")
            .get(0);
    assertEquals("I do not like this.", termRatingEntity.getProperty("comments-term"));
    assertEquals("numberOneId", termRatingEntity.getProperty("reviewer-id"));
    assertEquals(Long.valueOf(1), termRatingEntity.getProperty("perception-term"));
    assertEquals(Long.valueOf(8), termRatingEntity.getProperty("hours"));
    assertEquals(Long.valueOf(4), termRatingEntity.getProperty("difficulty"));
    assertEquals("The professor was amazing.", termRatingEntity.getProperty("comments-professor"));
    assertEquals(Long.valueOf(3), termRatingEntity.getProperty("perception-professor"));
    assertEquals(-0.8999999761581421, termRatingEntity.getProperty("score-term"));
    assertEquals(-0.8999999761581421, termRatingEntity.getProperty("score-professor"));
  }

  @Test
  public void addTermRating_overwritingExistingTermRating() throws IOException {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    request = Mockito.mock(HttpServletRequest.class);
    Entity termEntity = new Entity("Term");
    datastore.put(termEntity);
    Key termKey = termEntity.getKey();

    when(request.getParameter("term")).thenReturn(KeyFactory.keyToString(termKey));
    when(request.getParameter("term-input")).thenReturn("I really like this class.");
    when(request.getParameter("rating-term")).thenReturn("4");
    when(request.getParameter("hoursOfWork")).thenReturn("8");
    when(request.getParameter("difficulty")).thenReturn("4");
    when(request.getParameter("prof-input")).thenReturn("The professor was okay.");
    when(request.getParameter("rating-professor")).thenReturn("3");
    when(request.getParameter("ID")).thenReturn("numberOneId");

    AnalyzeSentimentResponse response =
        AnalyzeSentimentResponse.newBuilder()
            .setDocumentSentiment(Sentiment.newBuilder().setScore((float) -0.699999988079071))
            .build();
    when(languageService.analyzeSentiment(any(Document.class))).thenReturn(response);

    newTermRating.addTermRating(request);

    when(request.getParameter("term-input")).thenReturn("I don't like this class.");
    when(request.getParameter("rating-term")).thenReturn("1");
    when(request.getParameter("hoursOfWork")).thenReturn("10");
    when(request.getParameter("difficulty")).thenReturn("5");
    when(request.getParameter("prof-input")).thenReturn("This teacher was wonderful.");
    when(request.getParameter("rating-professor")).thenReturn("3");
    when(request.getParameter("ID")).thenReturn("numberOneId");
    when(request.getParameter("term")).thenReturn(KeyFactory.keyToString(termKey));

    newTermRating.addTermRating(request);

    Entity termRatingEntity =
        newTermRating
            .queryEntities(
                /* entityName */ "Rating",
                /* propertyName */ "reviewer-id",
                /* propertyValue */ "numberOneId")
            .get(0);
    assertEquals("I don't like this class.", termRatingEntity.getProperty("comments-term"));
    assertEquals("numberOneId", termRatingEntity.getProperty("reviewer-id"));
    assertEquals(Long.valueOf(1), termRatingEntity.getProperty("perception-term"));
    assertEquals(Long.valueOf(10), termRatingEntity.getProperty("hours"));
    assertEquals(Long.valueOf(5), termRatingEntity.getProperty("difficulty"));
    assertEquals("This teacher was wonderful.", termRatingEntity.getProperty("comments-professor"));
    assertEquals(Long.valueOf(3), termRatingEntity.getProperty("perception-professor"));
    assertEquals(-0.699999988079071, termRatingEntity.getProperty("score-term"));
    assertEquals(-0.699999988079071, termRatingEntity.getProperty("score-professor"));
  }
}
