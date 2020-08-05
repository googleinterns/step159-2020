// Copyright 2019 Google LLC
//
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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    boolean loggedIn = userService.isUserLoggedIn();
    String urlToRedirect = "/search.html";

    JSONObject userDetails = new JSONObject();
    userDetails.put("loggedIn", loggedIn);
    if (loggedIn) {
      String userEmail = userService.getCurrentUser().getEmail();
      userDetails.put("userEmail", userEmail);
      String logoutURL = userService.createLogoutURL(urlToRedirect);
      userDetails.put("logoutURL", logoutURL);
      // TODO: Implement OAuth for more robust email checking.
      int start = userEmail.indexOf('@');
      int end = userEmail.lastIndexOf('.');
      if (start != -1 && end != -1) {
        String schoolName = userEmail.substring(start + 1, end);
        userDetails.put("schoolName", schoolName);
      }

    } else {
      String loginURL = userService.createLoginURL(urlToRedirect);
      userDetails.put("loginURL", loginURL);
    }
    response.setContentType("application/json;");
    response.getWriter().println(userDetails);
  }
}