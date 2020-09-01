// package com.google.sps.servlets;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;

// import com.google.appengine.api.datastore.DatastoreService;
// import com.google.appengine.api.datastore.DatastoreServiceFactory;
// import com.google.appengine.api.datastore.Entity;
// import com.google.appengine.api.datastore.FetchOptions;
// import com.google.appengine.api.datastore.Key;
// import com.google.appengine.api.datastore.KeyFactory;
// import com.google.appengine.api.datastore.Query;
// import com.google.appengine.api.datastore.Query.Filter;
// import com.google.appengine.api.datastore.Query.FilterOperator;
// import com.google.appengine.api.datastore.Query.FilterPredicate;
// import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
// import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
// import com.google.cloud.language.v1.AnalyzeSentimentResponse;
// import com.google.cloud.language.v1.Document;
// import com.google.cloud.language.v1.LanguageServiceClient;
// import com.google.cloud.language.v1.Sentiment;
// import com.google.cloud.translate.v3.TranslateTextRequest;
// import com.google.cloud.translate.v3.TranslateTextResponse;
// import com.google.cloud.translate.v3.Translation;
// import com.google.cloud.translate.v3.TranslationServiceClient;
// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;
// import java.io.Reader;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.*;
// import java.util.List;
// import javax.servlet.http.HttpServletRequest;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.runner.RunWith;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.mockito.runners.MockitoJUnitRunner;

// @RunWith(MockitoJUnitRunner.class)
// public final class DataServletTest {

//   private static final LocalServiceTestHelper helper =
//       new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

//   private DataServlet dataServletInstance;
//   private String termKeyString;
//   private Key courseKey;

//   @BeforeEach
//   public void setUp() {
//     MockitoAnnotations.initMocks(this);
//     helper.setUp();
//     languageService = Mockito.mock(LanguageServiceClient.class);
//     translationService = Mockito.mock(TranslationServiceClient.class);
//     dataServletInstance = new DataServlet(languageService, translationService);
//   }

//   @AfterEach
//   public void tearDown() {
//     helper.tearDown();
//   }

//   @Mock LanguageServiceClient languageService;
//   @Mock TranslationServiceClient translationService;

//   @Test
//   public void addTermRating_newRating() throws IOException {
//     // SETUP.
//     // File with body request in webapp folder.
//     HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
//     Reader reader = new FileReader("src/main/webapp/WEB-INF/testNewRating.txt");
//     BufferedReader bufferedReader = new BufferedReader(reader);
//     when(request.getReader()).thenReturn(bufferedReader);

//     float sentimentScore = (float) -0.8999999761581421);
//     AnalyzeSentimentResponse response =
//         AnalyzeSentimentResponse.newBuilder()
//             .setDocumentSentiment(
//                 Sentiment.newBuilder()
//                     .setScore(sentimentScore) // Sentiment Score of Text.
//             .build();
//     when(languageService.analyzeSentiment(any(Document.class))).thenReturn(response);

//     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//     createSchoolCourseAndTermEntities(
//         datastore,
//         /* schoolName */ "google",
//         /* courseName */ "6.006",
//         /* termName */ "Spring 2020",
//         /* units */ "3");
//     addTermKeyTextFile(termKeyString, "src/main/webapp/WEB-INF/testNewRating.txt");

//     // ACT.
//     dataServletInstance.addTermRating(request, datastore);
//     Entity termRatingEntity =
//         dataServletInstance
//             .queryEntities(
//                 /* entityName */ "Rating",
//                 /* propertyName */ "reviewer-id",
//                 /* propertyValue */ "9223372036854775807",
//                 KeyFactory.stringToKey(termKeyString))
//             .get(0);

//     // ASSERT.
//     assertEquals("I do not like this.", termRatingEntity.getProperty("comments-term"));
//     assertEquals("B", termRatingEntity.getProperty("grade"));
//     assertEquals("9223372036854775807", termRatingEntity.getProperty("reviewer-id"));
//     assertEquals(Long.valueOf(1), termRatingEntity.getProperty("perception-term"));
//     assertEquals(Long.valueOf(8), termRatingEntity.getProperty("hours"));
//     assertEquals(Long.valueOf(4), termRatingEntity.getProperty("difficulty"));
//     assertEquals("The professor was amazing.",
// termRatingEntity.getProperty("comments-professor"));
//     assertEquals(Long.valueOf(3), termRatingEntity.getProperty("perception-professor"));
//     // Values of Sentiment Scores.
//     assertEquals((double) -0.8999999761581421, termRatingEntity.getProperty("score-term"));
//     assertEquals((double) -0.8999999761581421, termRatingEntity.getProperty("score-professor"));
//   }

//   @Test
//   public void addTermRating_overwritingExistingTermRating() throws IOException {
//     // SETUP.
//     // File with body request in webapp folder.
//     HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
//     Reader originalRatingReader = new FileReader("src/main/webapp/WEB-INF/testNewRating.txt");
//     BufferedReader originalRatingBufferedReader = new BufferedReader(originalRatingReader);
//     when(request.getReader()).thenReturn(originalRatingBufferedReader);

//     float sentimentScore = (float) -0.699999988079071);
//     AnalyzeSentimentResponse response =
//         AnalyzeSentimentResponse.newBuilder()
//             .setDocumentSentiment(
//                 Sentiment.newBuilder()
//                     .setScore(sentimentScore) // Sentiment Score of Text.
//             .build();
//     when(languageService.analyzeSentiment(any(Document.class))).thenReturn(response);

//     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//     createSchoolCourseAndTermEntities(
//         datastore,
//         /* schoolName */ "google",
//         /* courseName */ "6.006",
//         /* termName */ "Spring 2020",
//         /* units */ "3");
//     addTermKeyTextFile(termKeyString, "src/main/webapp/WEB-INF/testOverwriteRating.txt");

//     // ACT.
//     dataServletInstance.addTermRating(request, datastore);

//     // SETUP.
//     Reader newRatingReader = new FileReader("src/main/webapp/WEB-INF/testOverwriteRating.txt");
//     BufferedReader NewRatingBufferedReader = new BufferedReader(newRatingReader);
//     when(request.getReader()).thenReturn(NewRatingBufferedReader);

//     // ACT.
//     dataServletInstance.addTermRating(request, datastore);
//     Entity termRatingEntity =
//         dataServletInstance
//             .queryEntities(
//                 /* entityName */ "Rating",
//                 /* propertyName */ "reviewer-id",
//                 /* propertyValue */ "9223372036854775807",
//                 KeyFactory.stringToKey(termKeyString))
//             .get(0);

//     // ASSERT.
//     assertEquals("C", termRatingEntity.getProperty("grade"));
//     assertEquals("I don't like this class.", termRatingEntity.getProperty("comments-term"));
//     assertEquals("9223372036854775807", termRatingEntity.getProperty("reviewer-id"));
//     assertEquals(Long.valueOf(1), termRatingEntity.getProperty("perception-term"));
//     assertEquals(Long.valueOf(10), termRatingEntity.getProperty("hours"));
//     assertEquals(Long.valueOf(5), termRatingEntity.getProperty("difficulty"));
//     assertEquals("This teacher was wonderful.",
// termRatingEntity.getProperty("comments-professor"));
//     assertEquals(Long.valueOf(3), termRatingEntity.getProperty("perception-professor"));
//     // Values of Sentiment Scores.
//     assertEquals((double) -0.699999988079071, termRatingEntity.getProperty("score-term"));
//     assertEquals((double) -0.699999988079071, termRatingEntity.getProperty("score-professor"));
//   }

//   @Test
//   public void addTermRating_WithTranslation() throws IOException {
//     // SETUP.
//     // File with body request in webapp folder.
//     HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
//     Reader reader = new FileReader("src/main/webapp/WEB-INF/testTranslation.txt");
//     BufferedReader bufferedReader = new BufferedReader(reader);
//     when(request.getReader()).thenReturn(bufferedReader);

//     float sentimentScore = (float) -0.8999999761581421);
//     AnalyzeSentimentResponse response =
//         AnalyzeSentimentResponse.newBuilder()
//             .setDocumentSentiment(
//                 Sentiment.newBuilder()
//                     .setScore(sentimentScore) // Sentiment Score of Text.
//             .build();
//     when(languageService.analyzeSentiment(any(Document.class))).thenReturn(response);

//     TranslateTextResponse translateResponse =
//         TranslateTextResponse.newBuilder()
//             .addTranslations(
//                 Translation.newBuilder().setTranslatedText("I do not like this.").build())
//             .build();
//     when(translationService.translateText(any(TranslateTextRequest.class)))
//         .thenReturn(translateResponse);

//     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//     createSchoolCourseAndTermEntities(
//         datastore,
//         /* schoolName */ "google",
//         /* courseName */ "6.006",
//         /* termName */ "Spring 2020",
//         /* units */ "3");
//     addTermKeyTextFile(termKeyString, "src/main/webapp/WEB-INF/testNewRating.txt");

//     // ACT.
//     dataServletInstance.addTermRating(request, datastore);
//     Entity termRatingEntity =
//         dataServletInstance
//             .queryEntities(
//                 /* entityName */ "Rating",
//                 /* propertyName */ "reviewer-id",
//                 /* propertyValue */ "9223372036854775807",
//                 KeyFactory.stringToKey(termKeyString))
//             .get(0);

//     // ASSERT.
//     assertEquals("I do not like this.", termRatingEntity.getProperty("comments-term"));
//     assertEquals("B", termRatingEntity.getProperty("grade"));
//     assertEquals("9223372036854775807", termRatingEntity.getProperty("reviewer-id"));
//     assertEquals(Long.valueOf(1), termRatingEntity.getProperty("perception-term"));
//     assertEquals(Long.valueOf(8), termRatingEntity.getProperty("hours"));
//     assertEquals(Long.valueOf(4), termRatingEntity.getProperty("difficulty"));
//     assertEquals("I do not like this.", termRatingEntity.getProperty("comments-professor"));
//     assertEquals(Long.valueOf(3), termRatingEntity.getProperty("perception-professor"));
//     // Values of Sentiment Scores.
//     assertEquals((double) -0.8999999761581421, termRatingEntity.getProperty("score-term"));
//     assertEquals((double) -0.8999999761581421, termRatingEntity.getProperty("score-professor"));
//   }

//   @Test
//   public void addTermRating_SameCourse_DifferentTerms() throws IOException {
//     // SETUP.
//     // File with body request in webapp folder.
//     HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
//     Reader reader = new FileReader("src/main/webapp/WEB-INF/testNewRating.txt");
//     BufferedReader bufferedReader = new BufferedReader(reader);
//     when(request.getReader()).thenReturn(bufferedReader);

//     float sentimentScore = (float) -0.8999999761581421);
//     AnalyzeSentimentResponse response =
//         AnalyzeSentimentResponse.newBuilder()
//             .setDocumentSentiment(
//                 Sentiment.newBuilder()
//                     .setScore(sentimentScore) // Sentiment Score of Text.
//             .build();
//     when(languageService.analyzeSentiment(any(Document.class))).thenReturn(response);

//     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//     createSchoolCourseAndTermEntities(
//         datastore,
//         /* schoolName */ "google",
//         /* courseName */ "6.006",
//         /* termName */ "Spring 2020",
//         /* units */ "3");
//     addTermKeyTextFile(termKeyString, "src/main/webapp/WEB-INF/testNewRating.txt");

//     // ACT.
//     dataServletInstance.addTermRating(request, datastore);

//     Entity termRatingEntity =
//         dataServletInstance
//             .queryEntities(
//                 /* entityName */ "Rating",
//                 /* propertyName */ "reviewer-id",
//                 /* propertyValue */ "9223372036854775807",
//                 KeyFactory.stringToKey(termKeyString))
//             .get(0);

//     // SET UP.
//     addNewTermToSameCourse(datastore, courseKey, "Fall 2019");
//     addTermKeyTextFile(termKeyString, "src/main/webapp/WEB-INF/testNewRating.txt");
//     Reader newRatingReader = new FileReader("src/main/webapp/WEB-INF/testNewRating.txt");
//     BufferedReader NewRatingBufferedReader = new BufferedReader(newRatingReader);
//     when(request.getReader()).thenReturn(NewRatingBufferedReader);

//     // ACT.
//     dataServletInstance.addTermRating(request, datastore);
//     Entity newTermRatingEntity =
//         dataServletInstance
//             .queryEntities(
//                 /* entityName */ "Rating",
//                 /* propertyName */ "reviewer-id",
//                 /* propertyValue */ "9223372036854775807",
//                 KeyFactory.stringToKey(termKeyString))
//             .get(0);

//     // ASSERT.
//     assertEquals("I do not like this.", termRatingEntity.getProperty("comments-term"));
//     assertEquals("B", termRatingEntity.getProperty("grade"));
//     assertEquals("9223372036854775807", termRatingEntity.getProperty("reviewer-id"));
//     assertEquals(Long.valueOf(1), termRatingEntity.getProperty("perception-term"));
//     assertEquals(Long.valueOf(8), termRatingEntity.getProperty("hours"));
//     assertEquals(Long.valueOf(4), termRatingEntity.getProperty("difficulty"));
//     assertEquals("The professor was amazing.",
// termRatingEntity.getProperty("comments-professor"));
//     assertEquals(Long.valueOf(3), termRatingEntity.getProperty("perception-professor"));
//     // Values of Sentiment Scores.
//     assertEquals((double) -0.8999999761581421, termRatingEntity.getProperty("score-term"));
//     assertEquals((double) -0.8999999761581421, termRatingEntity.getProperty("score-professor"));

//     assertEquals("I do not like this.", newTermRatingEntity.getProperty("comments-term"));
//     assertEquals("B", newTermRatingEntity.getProperty("grade"));
//     assertEquals("9223372036854775807", newTermRatingEntity.getProperty("reviewer-id"));
//     assertEquals(Long.valueOf(1), newTermRatingEntity.getProperty("perception-term"));
//     assertEquals(Long.valueOf(8), newTermRatingEntity.getProperty("hours"));
//     assertEquals(Long.valueOf(4), newTermRatingEntity.getProperty("difficulty"));
//     assertEquals(
//         "The professor was amazing.", newTermRatingEntity.getProperty("comments-professor"));
//     assertEquals(Long.valueOf(3), newTermRatingEntity.getProperty("perception-professor"));
//     // Values of Sentiment Scores.
//     assertEquals((double) -0.8999999761581421, newTermRatingEntity.getProperty("score-term"));
//     assertEquals((double) -0.8999999761581421,
// newTermRatingEntity.getProperty("score-professor"));
//   }

//   @Test
//   public void addTermRating_DifferentCourse_SameTerm() throws IOException {
//     // SETUP.
//     // File with body request in webapp folder.
//     HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
//     Reader reader = new FileReader("src/main/webapp/WEB-INF/testNewRating.txt");
//     BufferedReader bufferedReader = new BufferedReader(reader);
//     when(request.getReader()).thenReturn(bufferedReader);

//     float sentimentScore = (float) -0.8999999761581421;
//     AnalyzeSentimentResponse response =
//         AnalyzeSentimentResponse.newBuilder()
//             .setDocumentSentiment(
//                 Sentiment.newBuilder()
//                     .setScore(sentimentScore) // Sentiment Score of Text.
//             .build();
//     when(languageService.analyzeSentiment(any(Document.class))).thenReturn(response);

//     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//     createSchoolCourseAndTermEntities(
//         datastore,
//         /* schoolName */ "google",
//         /* courseName */ "6.006",
//         /* termName */ "Spring 2020",
//         /* units */ "3");
//     addTermKeyTextFile(termKeyString, "src/main/webapp/WEB-INF/testNewRating.txt");

//     // ACT.
//     dataServletInstance.addTermRating(request, datastore);
//     Entity termRatingEntity =
//         dataServletInstance
//             .queryEntities(
//                 /* entityName */ "Rating",
//                 /* propertyName */ "reviewer-id",
//                 /* propertyValue */ "9223372036854775807",
//                 KeyFactory.stringToKey(termKeyString))
//             .get(0);

//     // SET UP.
//     addDifferentCourseSameTerm(
//         datastore,
//         /* schoolName */ "google",
//         /* courseName */ "6.004",
//         /* termName */ "Spring 2020",
//         /* units */ "3");
//     addTermKeyTextFile(termKeyString, "src/main/webapp/WEB-INF/testNewRating.txt");
//     Reader newRatingReader = new FileReader("src/main/webapp/WEB-INF/testNewRating.txt");
//     BufferedReader NewRatingBufferedReader = new BufferedReader(newRatingReader);
//     when(request.getReader()).thenReturn(NewRatingBufferedReader);

//     // ACT.
//     dataServletInstance.addTermRating(request, datastore);
//     Entity newTermRatingEntity =
//         dataServletInstance
//             .queryEntities(
//                 /* entityName */ "Rating",
//                 /* propertyName */ "reviewer-id",
//                 /* propertyValue */ "9223372036854775807",
//                 KeyFactory.stringToKey(termKeyString))
//             .get(0);

//     // ASSERT.
//     assertEquals("I do not like this.", termRatingEntity.getProperty("comments-term"));
//     assertEquals("B", termRatingEntity.getProperty("grade"));
//     assertEquals("9223372036854775807", termRatingEntity.getProperty("reviewer-id"));
//     assertEquals(Long.valueOf(1), termRatingEntity.getProperty("perception-term"));
//     assertEquals(Long.valueOf(8), termRatingEntity.getProperty("hours"));
//     assertEquals(Long.valueOf(4), termRatingEntity.getProperty("difficulty"));
//     assertEquals("The professor was amazing.",
// termRatingEntity.getProperty("comments-professor"));
//     assertEquals(Long.valueOf(3), termRatingEntity.getProperty("perception-professor"));
//     // Values of Sentiment Scores.
//     assertEquals((double) -0.8999999761581421, termRatingEntity.getProperty("score-term"));
//     assertEquals((double) -0.8999999761581421, termRatingEntity.getProperty("score-professor"));

//     assertEquals("I do not like this.", newTermRatingEntity.getProperty("comments-term"));
//     assertEquals("B", newTermRatingEntity.getProperty("grade"));
//     assertEquals("9223372036854775807", newTermRatingEntity.getProperty("reviewer-id"));
//     assertEquals(Long.valueOf(1), newTermRatingEntity.getProperty("perception-term"));
//     assertEquals(Long.valueOf(8), newTermRatingEntity.getProperty("hours"));
//     assertEquals(Long.valueOf(4), newTermRatingEntity.getProperty("difficulty"));
//     assertEquals(
//         "The professor was amazing.", newTermRatingEntity.getProperty("comments-professor"));
//     assertEquals(Long.valueOf(3), newTermRatingEntity.getProperty("perception-professor"));
//     // Values of Sentiment Scores.
//     assertEquals((double) -0.8999999761581421, newTermRatingEntity.getProperty("score-term"));
//     assertEquals((double) -0.8999999761581421,
// newTermRatingEntity.getProperty("score-professor"));
//   }

//   public void createSchoolCourseAndTermEntities(
//       DatastoreService datastore,
//       String schoolName,
//       String courseName,
//       String termName,
//       String units) {
//     Entity schoolEntity = new Entity("School");
//     schoolEntity.setProperty("school-name", schoolName);
//     datastore.put(schoolEntity);

//     Entity courseEntity = new Entity("Course", schoolEntity.getKey());
//     courseEntity.setProperty("course-name", courseName);
//     courseEntity.setProperty("units", Long.valueOf(units));
//     datastore.put(courseEntity);
//     courseKey = courseEntity.getKey();

//     Entity termEntity = new Entity("Term", courseEntity.getKey());
//     termEntity.setProperty("term", termName);
//     datastore.put(termEntity);
//     termKeyString = getTermKeyString(datastore, termName, courseEntity.getKey());
//   }

//   private String getTermKeyString(DatastoreService datastore, String term, Key courseKey) {
//     Filter termFilter = new FilterPredicate("term", FilterOperator.EQUAL, term);
//     Query termQuery = new Query("Term").setAncestor(courseKey).setFilter(termFilter);
//     Entity queryTermResult =
//         datastore.prepare(termQuery).asList(FetchOptions.Builder.withDefaults()).get(0);
//     return KeyFactory.keyToString(queryTermResult.getKey());
//   }

//   private void addTermKeyTextFile(String termKey, String filePath) throws IOException {
//     Path path = Paths.get(filePath);
//     List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
//     lines.set(1, "  termKey : " + termKey + ",");
//     Files.write(path, lines, StandardCharsets.UTF_8);
//   }

//   private void addNewTermToSameCourse(DatastoreService datastore, Key courseKey, String termName)
// {
//     Entity newTermEntity = new Entity("Term", courseKey);
//     newTermEntity.setProperty("term", termName);
//     datastore.put(newTermEntity);
//     termKeyString = KeyFactory.keyToString(newTermEntity.getKey());
//   }

//   private void addDifferentCourseSameTerm(
//       DatastoreService datastore,
//       String schoolName,
//       String termName,
//       String courseName,
//       String units) {
//     Filter schoolFilter = new FilterPredicate("school-name", FilterOperator.EQUAL, schoolName);
//     Query schoolQuery = new Query("School").setFilter(schoolFilter);
//     Entity schoolQueryEntity =
//         datastore.prepare(schoolQuery).asList(FetchOptions.Builder.withDefaults()).get(0);
//     // Creates new course.
//     Entity newCourseEntity = new Entity("Course", schoolQueryEntity.getKey());
//     newCourseEntity.setProperty("course-name", courseName);
//     newCourseEntity.setProperty("units", Long.valueOf(units));
//     datastore.put(newCourseEntity);
//     // Creates term.
//     Entity newTermEntity = new Entity("Term", newCourseEntity.getKey());
//     newTermEntity.setProperty("term", termName);
//     datastore.put(newTermEntity);
//     termKeyString = KeyFactory.keyToString(newTermEntity.getKey());
//   }
// }
