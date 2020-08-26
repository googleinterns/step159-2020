// Copyright 2019 Google LLC
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/** Servlet that finds out if a school is on the quarter system or semester system. */
@WebServlet("/term")
public class TermServlet extends HttpServlet {

  static final List<String> QTR_SCHOOLS =
      Arrays.asList("caltech", "calpoly", "stanford", "berkeley");

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Boolean quarter = isQuarter(request.getParameter("school-name"));
    JSONObject json = new JSONObject();
    json.put("quarter", quarter);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /* Return term system of a school. */
  public static Boolean isQuarter(String schoolName) {
    return QTR_SCHOOLS.contains(schoolName);
  }
}
