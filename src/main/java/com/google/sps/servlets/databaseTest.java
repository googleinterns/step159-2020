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
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/database-make")
public class databaseTest extends HttpServlet {

  private Key existingSchoolKey;
  private Key existingCourseKey;
  private Key existingProfessorKey;

  private Key newSchoolKey = null;
  private Key newCourseKey = null;
  private Key newProfessorKey = null;
  private Key newTermKey = null;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String hey = "hello";
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String schoolName = request.getParameter("school-name");
    String courseName = request.getParameter("course-name");
    String term = request.getParameter("term");
    String units = request.getParameter("units");
    String profName = request.getParameter("professor-name");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // sees if school exists if it does grabs key, else makes one
    Filter schoolFilter = new FilterPredicate("school-name", FilterOperator.EQUAL, schoolName);
    Query schoolQ = new Query("School").setFilter(schoolFilter);
    List<Entity> schoolQuery =
        datastore.prepare(schoolQ).asList(FetchOptions.Builder.withDefaults());

    System.out.println(schoolQuery);
    System.out.println(schoolQuery.size());

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

    System.out.println(newSchoolKey);

    // new school so everything is new
    if (newSchoolKey != null) {

      System.out.println("NEW EVEYTHING");

      Entity newCourse = new Entity("Course", newSchoolKey);
      newCourse.setProperty("course-name", courseName);
      newCourse.setProperty("Units", units);
      newCourseKey = newCourse.getKey();
      datastore.put(newCourse);

      Entity newTerm = new Entity("Term", newCourseKey);
      newTerm.setProperty("term", term);
      newTerm.setProperty("instructor", profName);
      newTermKey = newTerm.getKey();
      datastore.put(newTerm);

      Entity newProfessor = new Entity("Professor", newSchoolKey);
      newProfessor.setProperty("name", profName);
      newProfessor.setProperty("courses", newCourseKey);
      newProfessor.setProperty("course-list", (courseName + ", " + term));

    } else {
      System.out.println("Old School");

      // existing school so now we check if this is a new course
      Filter courseFilter = new FilterPredicate("course-name", FilterOperator.EQUAL, courseName);
      Query courseQ = new Query("Course").setAncestor(existingSchoolKey).setFilter(courseFilter);
      List<Entity> courseQuery =
          datastore.prepare(courseQ).asList(FetchOptions.Builder.withDefaults());

      System.out.println("THIS SHOULD PRINT AFTER OLD SHCOOL IS FOUND");
      System.out.println(courseQuery);

      if (courseQuery.size() == 0) {

        System.out.println("Old School NEW COURSE");
        // new course
        Entity newCourse;
        if (existingSchoolKey != null) {
          newCourse = new Entity("Course", existingSchoolKey);
        } else {
          newCourse = new Entity("Course", newSchoolKey);
        }

        newCourse.setProperty("course-name", courseName);
        newCourse.setProperty("Units", units);
        datastore.put(newCourse);

        // there should never be duplicate terms so no need to check
        Entity newTerm = new Entity("Term", newCourseKey);
        newTerm.setProperty("term", term);
        newTerm.setProperty("instructor", profName);
        newTermKey = newTerm.getKey();
        datastore.put(newTerm);

        // professor are tied to terms so we should check them last
        Filter professorFilter =
            new FilterPredicate("professor-name", FilterOperator.EQUAL, profName);
        Query professorQ =
            new Query("Professor").setAncestor(existingSchoolKey).setFilter(professorFilter);
        List<Entity> professorQuery =
            datastore.prepare(professorQ).asList(FetchOptions.Builder.withDefaults());

        // this is a new professor
        if (professorQuery.size() == 0) {
          System.out.println("OLD SCHOOL NEW COURSE NEW PROFESSOR");
          Entity newProfessor;
          newProfessor = new Entity("Professor", existingSchoolKey);
          newProfessor.setProperty("professor-name", profName);
          newProfessor.setProperty("school", schoolName);
          newProfessor.setProperty("teaches", newTermKey);
          newProfessorKey = newProfessor.getKey();
          datastore.put(newProfessor);
        } else {
          System.out.println("OLD SCHOOL NEW COURSE OLD PROFESSOR");
          System.out.println("OLD PROFESSOR");
        }
      } else {

        System.out.println("Old School OLD COURSE");
        // no new course only new term
        Entity existingCourse = courseQuery.get(0);
        existingCourseKey = existingCourse.getKey();

        Entity newTerm = new Entity("Term", existingCourseKey);
        newTerm.setProperty("term", term);
        newTerm.setProperty("instructor", profName);
        newTermKey = newTerm.getKey();
        datastore.put(newTerm);

        // professor are tied to terms so we should check them last
        Filter professorFilter =
            new FilterPredicate("professor-name", FilterOperator.EQUAL, profName);
        Query professorQ =
            new Query("Professor").setAncestor(existingSchoolKey).setFilter(professorFilter);
        List<Entity> professorQuery =
            datastore.prepare(professorQ).asList(FetchOptions.Builder.withDefaults());

        // this is a new professor
        if (professorQuery.size() == 0) {
          System.out.println("OLD SCHOOL OLD COURSE NEW PROFESSOR");
          Entity newProfessor;
          newProfessor = new Entity("Professor", existingSchoolKey);
          newProfessor.setProperty("professor-name", profName);
          newProfessor.setProperty("school", schoolName);
          newProfessor.setProperty("teaches", newTermKey);
          newProfessorKey = newProfessor.getKey();
          datastore.put(newProfessor);
        } else {
          System.out.println("OLD SCHOOL OLD COURSE OLD PROFESSOR");
          System.out.println("OLD PROFESSOR");
        }
      }
    }
  }

  /**
   * @return the request parameter, or the default value if the parameter was not specified by the
   *     client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    return value != null ? value : defaultValue;
  }
}
