package com.google.sps.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.sps.data.GraphDataObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/graph")
public class ChartsServlet extends HttpServlet {
  private List<Object> entryList = new ArrayList<>();
  GraphDataObject staticEntry = new GraphDataObject("Hours", "Difficulty");

  @Override
  public void init() {
    Scanner scannerHours = new Scanner(getServletContext().getResourceAsStream("/hours.csv"));
    Scanner scannerDifficulty =
        new Scanner(getServletContext().getResourceAsStream("/difficulty.csv"));

    while (scannerHours.hasNextLine()) {
      String line = scannerHours.nextLine();
      staticEntry.addHour(line);
    }
    scannerHours.close();

    while (scannerDifficulty.hasNextLine()) {
      String line = scannerDifficulty.nextLine();
      staticEntry.addDifficulty(line);
    }
    scannerDifficulty.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String dataJSON = makeJSON(staticEntry);
    response.setContentType("application/json;");
    response.getWriter().println(dataJSON);
  }

  private String makeJSON(Object changeItem) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      String jsonString = mapper.writeValueAsString(changeItem);
      return jsonString;
    } catch (Exception e) {
      return "Could not convert to JSON";
    }
  }
}
