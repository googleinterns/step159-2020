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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that logs user activity. */
@WebServlet("/authentication")
public class AuthenticationServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String email = request.getParameter("email");
    String status = request.getParameter("status");
    Filter emailFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
    Query userQuery = new Query("User");
    userQuery.setFilter(emailFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(userQuery).asList(FetchOptions.Builder.withDefaults());

    if (status.equals("logged-in")) {
      String name = request.getParameter("name");
      String id = request.getParameter("id");
      if (results.isEmpty()) {
        Entity newUser = new Entity("User");
        newUser.setProperty("name", name);
        newUser.setProperty("email", email);
        newUser.setProperty("id", id);
        newUser.setProperty("status", status);
        datastore.put(newUser);
      } else {
        Entity existingUser = results.get(0);
        existingUser.setProperty("status", status);
        datastore.put(existingUser);
      }
    } else if (status.equals("logged-out")) {
      Entity loggingOutUser = results.get(0);
      loggingOutUser.setProperty("status", status);
      datastore.put(loggingOutUser);
    }
    response.sendRedirect("/search.html");
  }
}
