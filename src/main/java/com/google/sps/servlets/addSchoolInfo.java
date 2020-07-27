package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.sps.data.PostRequestObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/add-info")
public class addSchoolInfo extends HttpServlet {

  private Key existingSchoolKey;
  private Key existingCourseKey;
  private Key existingProfessorKey;

  private Key newSchoolKey;
  private Key newCourseKey;
  private Key newProfessorKey;
  private Key newTermKey;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    PostRequestObject postRequestData = processPostRequest(request);
    String schoolName = postRequestData.getSchool();
    String courseName = postRequestData.getCourse();
    String term = postRequestData.getTerm();
    String units = postRequestData.getUnits();
    String profName = postRequestData.getProf();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Filter schoolFilter = new FilterPredicate("school-name", FilterOperator.EQUAL, schoolName);
    Query schoolQ = new Query("School").setFilter(schoolFilter);
    List<Entity> schoolQuery =
        datastore.prepare(schoolQ).asList(FetchOptions.Builder.withDefaults());

    if (schoolQuery.size() == 0) {
      Entity newSchool = new Entity("School");
      newSchool.setProperty("school-name", schoolName);
      newSchoolKey = newSchool.getKey();
      datastore.put(newSchool);
    } else {
      Entity existingSchool = schoolQuery.get(0);
      existingSchoolKey = existingSchool.getKey();
      newSchoolKey = null;
    }

    if (newSchoolKey != null) {
      Entity newCourse = new Entity("Course", newSchoolKey);
      newCourse.setProperty("course-name", courseName);
      newCourse.setProperty("Units", units);
      newCourseKey = newCourse.getKey();
      datastore.put(newCourse);

      Entity newProfessor;
      newProfessor = new Entity("Professor", newSchoolKey);
      newProfessor.setProperty("professor-name", profName);
      newProfessorKey = newProfessor.getKey();
      datastore.put(newProfessor);

      Entity newTerm = new Entity("Term", newCourseKey);
      newTerm.setProperty("term", term);
      newTerm.setProperty("Professor", newProfessorKey);
      datastore.put(newTerm);

    } else {
      Filter courseFilter = new FilterPredicate("course-name", FilterOperator.EQUAL, courseName);
      Query courseQ = new Query("Course").setAncestor(existingSchoolKey).setFilter(courseFilter);
      List<Entity> courseQuery =
          datastore.prepare(courseQ).asList(FetchOptions.Builder.withDefaults());

      // existing school --> new course
      if (courseQuery.size() == 0) {
        Entity newCourse;
        if (existingSchoolKey != null) {
          newCourse = new Entity("Course", existingSchoolKey);
        } else {
          newCourse = new Entity("Course", newSchoolKey);
        }

        newCourse.setProperty("course-name", courseName);
        newCourse.setProperty("Units", units);
        datastore.put(newCourse);

        Entity newTerm = new Entity("Term", newCourseKey);
        newTerm.setProperty("term", term);

        Filter professorFilter =
            new FilterPredicate("professor-name", FilterOperator.EQUAL, profName);
        Query professorQ =
            new Query("Professor").setAncestor(existingSchoolKey).setFilter(professorFilter);
        List<Entity> professorQuery =
            datastore.prepare(professorQ).asList(FetchOptions.Builder.withDefaults());

        // existing school --> new course --> new professor
        if (professorQuery.size() == 0) {
          System.out.println("OLD SCHOOL NEW COURSE NEW PROFESSOR");
          Entity newProfessor;
          newProfessor = new Entity("Professor", existingSchoolKey);
          newProfessor.setProperty("professor-name", profName);
          newProfessorKey = newProfessor.getKey();
          datastore.put(newProfessor);

          newTerm.setProperty("Professor", newProfessorKey);
          datastore.put(newTerm);

        } else {
          // existing school --> new course --> existing professor
          Entity existingProfessor = professorQuery.get(0);
          existingProfessorKey = existingProfessor.getKey();

          newTerm.setProperty("Professor", newProfessorKey);
          datastore.put(newTerm);
        }
      } else {
        // existing school --> existing course --> existing professor
        Entity existingCourse = courseQuery.get(0);
        existingCourseKey = existingCourse.getKey();

        Entity newTerm = new Entity("Term", existingCourseKey);
        newTerm.setProperty("term", term);

        Filter professorFilter =
            new FilterPredicate("professor-name", FilterOperator.EQUAL, profName);
        Query professorQ =
            new Query("Professor").setAncestor(existingSchoolKey).setFilter(professorFilter);
        List<Entity> professorQuery =
            datastore.prepare(professorQ).asList(FetchOptions.Builder.withDefaults());

        // existing school --> existing course --> new professor
        if (professorQuery.size() == 0) {
          Entity newProfessor;
          newProfessor = new Entity("Professor", existingSchoolKey);
          newProfessor.setProperty("professor-name", profName);
          newProfessorKey = newProfessor.getKey();
          datastore.put(newProfessor);

          newTerm.setProperty("Professor", newProfessorKey);
          datastore.put(newTerm);

        } else {
          // existing school --> existing course --> existing professor
          Entity existingProfessor = professorQuery.get(0);
          existingProfessorKey = existingProfessor.getKey();
          newTerm.setProperty("Professor", existingProfessorKey);
          datastore.put(newTerm);
        }
      }
    }

    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.sendRedirect("/addSchoolInfo.html");
  }

  public PostRequestObject processPostRequest(HttpServletRequest request) {
    String schoolName = request.getParameter("school-name");
    String courseName = request.getParameter("course-name");
    String term = request.getParameter("term");
    String units = request.getParameter("units");
    String profName = request.getParameter("professor-name");

    PostRequestObject newPost =
        new PostRequestObject(schoolName, courseName, term, units, profName);
    return newPost;
  }
}
