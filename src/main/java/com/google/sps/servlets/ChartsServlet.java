package com.google.sps.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.sps.data.EntryObject;
import java.io.IOException;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/graph")
public class ChartsServlet extends HttpServlet {
  EntryObject graphData = new EntryObject();

  @Override
  public void init() {
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/hours.csv"));
    Scanner scannerDifficulty =
        new Scanner(getServletContext().getResourceAsStream("/difficulty.csv"));

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");
      for (String num : cells) {
        graphData.addHour(num);
      }
    }
    scanner.close();

    while (scannerDifficulty.hasNextLine()) {
      String line = scannerDifficulty.nextLine();
      String[] cells2 = line.split(",");
      for (String num2 : cells2) {
        graphData.addDifficulty(num2);
      }
      scannerDifficulty.close();
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String dataJSON = makeJSON(graphData);
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

// import com.google.appengine.api.datastore.Text;
// import com.google.sps.data.EntryObject;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Scanner;
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// @WebServlet("/graph")
// public class ChartsServlet extends HttpServlet {

//   private List<Object> entryList = new ArrayList<>();
//   private String covidData;

//   @Override
//   public void init() {
//     Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/hours.csv"));
//     while (scanner.hasNextLine()) {
//       String line = scanner.nextLine();
//       String[] cells = line.split(",");
//       String[] dateNums = cells[0].split("-");

//       Integer year = Integer.parseInt(dateNums[0]);
//       Integer month = Integer.parseInt(dateNums[1]);
//       Integer day = Integer.parseInt(dateNums[2]);
//       Integer cases = Integer.parseInt(cells[1]);
//       Integer deaths = Integer.parseInt(cells[2]);

//       EntryObject newEntry = new EntryObject(year, month, day, cases, deaths);
//       entryList.add(newEntry);
//     }
//     scanner.close();
//     covidData = (makeJSON(entryList));
//   }

//   @Override
//   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
// {
//     response.setContentType("application/json;");
//     response.getWriter().println(covidData);
//   }

//   private String makeJSON(Object changeItem) {
//     try {
//       ObjectMapper mapper = new ObjectMapper();
//       String jsonString = mapper.writeValueAsString(changeItem);
//       return jsonString;
//     } catch (Exception e) {
//       return "Could not convert to JSON";
//     }
//   }
// }
