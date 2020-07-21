package com.google.sps.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.sps.data.EntryObject;
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
  EntryObject newEntry = new EntryObject("Hours", "Difficulty");

  @Override
  public void init() {
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/hours.csv"));
    Scanner scannerDifficulty =
        new Scanner(getServletContext().getResourceAsStream("/difficulty.csv"));

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      newEntry.addHour(line);
    }
    scanner.close();

    while (scannerDifficulty.hasNextLine()) {
      String line = scannerDifficulty.nextLine();
      newEntry.addDifficulty(line);
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String dataJSON = makeJSON(newEntry);
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
