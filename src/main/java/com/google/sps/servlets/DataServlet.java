package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

/** An item on a todo list. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private final List<Object> commentsList = new ArrayList<>();
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private LanguageServiceClient languageService;

  public DataServlet() throws IOException {
    this.languageService = LanguageServiceClient.create();
  }

  public DataServlet(LanguageServiceClient languageServiceClient) {
    this.languageService = languageServiceClient;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Rating").addSort("score-term", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      String comment = (String) entity.getProperty("comments-term");
      commentsList.add(comment);
    }

    // Send JSON string.
    String jsonVersionCommentsList = new Gson().toJson(commentsList);
    commentsList.clear();
    response.setContentType("application/json;");
    response.getWriter().println(jsonVersionCommentsList);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get written feedback.
    addTermRating(request, datastore);
    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
  }

  public void addTermRating(HttpServletRequest request, DatastoreService datastore)
      throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    String line = null;
    // Talked with Luke and as we won't do validation testing, I will not
    // test either of the try-catch loops.
    try {
      BufferedReader reader = request.getReader();
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
      }
    } catch (Exception exception) {
      throw new IOException("Error reading body of request");
    }

    String termKeyString;
    String termFeedback;
    String professorFeedback;
    Long termRating;
    Long professorRating;
    Long workHours;
    Long difficulty;
    String userId;
    String grade;
    Boolean translate;
    try {
      JSONObject jsonObject = new JSONObject(stringBuilder.toString());
      termKeyString = jsonObject.getString("termKey");
      termFeedback = jsonObject.getString("termInput");
      professorFeedback = jsonObject.getString("profInput");
      termRating = (long) jsonObject.getFloat("ratingTerm");
      professorRating = (long) jsonObject.getFloat("ratingProf");
      workHours = (long) jsonObject.getFloat("hours");
      difficulty = (long) jsonObject.getFloat("difficulty");
      grade = jsonObject.getString("grade");
      userId = jsonObject.getString("id");
      translate = Boolean.parseBoolean(jsonObject.getString("translate"));
    } catch (JSONException exception) {
      throw new IOException("Error parsing JSON request string");
    }

    if (translate) {
      termFeedback = translateTextToEnglish(termFeedback);
      professorFeedback = translateTextToEnglish(professorFeedback);
    }

    float termScore = getSentimentScore(termFeedback);
    float professorScore = getSentimentScore(professorFeedback);

    double toxicityTermComment = getToxicityScore(termFeedback);
    double toxicityProfComment = getToxicityScore(professorFeedback);
    if (toxicityTermComment >= 0.90) {
      termFeedback = "Could not show comment due to toxicity.";
    }
    if (toxicityProfComment >= 0.90) {
      professorFeedback = "Could not show comment due to toxicity.";
    }

    // Check whether user has reviewed that term.
    List<Entity> termRatingQueryList =
        queryEntities(
            /* entityName */ "Rating",
            /* propertyName */ "reviewer-id",
            /* propertyValue */ userId,
            KeyFactory.stringToKey(termKeyString));

    Entity termRatingEntity =
        termRatingQueryList.isEmpty()
            ? new Entity("Rating", KeyFactory.stringToKey(termKeyString))
            : termRatingQueryList.get(0);

    termRatingEntity.setProperty("comments-term", termFeedback);
    termRatingEntity.setProperty("toxicity-term-comments", toxicityTermComment);
    termRatingEntity.setProperty("toxicity-prof-comments", toxicityProfComment);
    termRatingEntity.setProperty("grade", grade);
    termRatingEntity.setProperty("reviewer-id", userId);
    termRatingEntity.setProperty("score-term", termScore);
    termRatingEntity.setProperty("perception-term", termRating);
    termRatingEntity.setProperty("hours", workHours);
    termRatingEntity.setProperty("difficulty", difficulty);
    termRatingEntity.setProperty("comments-professor", professorFeedback);
    termRatingEntity.setProperty("score-professor", professorScore);
    termRatingEntity.setProperty("perception-professor", professorRating);
    datastore.put(termRatingEntity);
  }

  private float getSentimentScore(String feedback) throws IOException {
    Document feedbackDoc =
        Document.newBuilder().setContent(feedback).setType(Document.Type.PLAIN_TEXT).build();
    Sentiment sentiment = this.languageService.analyzeSentiment(feedbackDoc).getDocumentSentiment();
    float score = sentiment.getScore();
    // Won't be closing languageService as we want to use constructor.
    return score;
  }

  public List<Entity> queryEntities(
      String entityName, String propertyName, String propertyValue, Key ancestorKey)
      throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Filter filter = new FilterPredicate(propertyName, FilterOperator.EQUAL, propertyValue);
    Query query = new Query(entityName).setFilter(filter).setAncestor(ancestorKey);
    // This is initialized when authentication happens, so should not be empty.
    List<Entity> queryList = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    return queryList;
  }

  private double getToxicityScore(String comment) throws IOException {
    HttpURLConnection commentAnalyzerConnection = creatingPostRequestCommentAnalyzer();
    // Create JSON request.
    JSONObject responseAnalysisRequest = getResponseAnalysisRequest(comment);

    OutputStreamWriter writer = new OutputStreamWriter(commentAnalyzerConnection.getOutputStream());
    writer.write(responseAnalysisRequest.toString());
    writer.close();

    int responseCode =
        commentAnalyzerConnection.getResponseCode(); // New items get NOT_FOUND on POST.
    if (responseCode == HttpURLConnection.HTTP_OK
        || responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
      StringBuilder response = new StringBuilder();
      String line;

      // Read input data stream.
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(commentAnalyzerConnection.getInputStream()));
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
      reader.close();
      JSONObject responseJsonObject = new JSONObject(response.toString());
      double toxicityValue =
          responseJsonObject
              .getJSONObject("attributeScores")
              .getJSONObject("TOXICITY")
              .getJSONObject("summaryScore")
              .getFloat("value");
      return toxicityValue;
    } else {
      throw new RuntimeException("Cannot get toxicity score.");
    }
  }

  private HttpURLConnection creatingPostRequestCommentAnalyzer() throws IOException {
    URL urlCommentAnalyzer =
        new URL(
            "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key="
                + System.getenv("API_KEY"));
    HttpURLConnection connection = (HttpURLConnection) urlCommentAnalyzer.openConnection();
    // Enable output for the connection.
    connection.setDoOutput(true);
    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    connection.setRequestProperty("Accept", "application/json");
    connection.setReadTimeout(10000);
    // Set HTTP request method.
    connection.setRequestMethod("POST");
    return connection;
  }

  private JSONObject getResponseAnalysisRequest(String text) throws IOException {
    JSONObject jsonObject = new JSONObject();
    JSONObject textJsonObject = new JSONObject();
    JSONObject toxicityJsonObject = new JSONObject();
    JSONObject toxicityScoreJsonObject = new JSONObject();
    jsonObject.put("comment", textJsonObject.put("text", text));
    jsonObject.put("languages", Arrays.asList("en"));
    jsonObject.put(
        "requestedAttributes", toxicityJsonObject.put("TOXICITY", toxicityScoreJsonObject));
    return jsonObject;
  }

  private String translateTextToEnglish(String text) throws IOException {
    String projectId = "nina-laura-dagm-step-2020";
    try (TranslationServiceClient client = TranslationServiceClient.create()) {
      LocationName parent = LocationName.of(projectId, "global");

      TranslateTextRequest request =
          TranslateTextRequest.newBuilder()
              .setParent(parent.toString())
              .setMimeType("text/plain")
              .setTargetLanguageCode("en")
              .addContents(text)
              .build();

      TranslateTextResponse response = client.translateText(request);
      return response.getTranslationsList().get(0).getTranslatedText();

    } catch (IOException exception) {
      throw new IOException("Could not translate comments");
    }
  }
}
