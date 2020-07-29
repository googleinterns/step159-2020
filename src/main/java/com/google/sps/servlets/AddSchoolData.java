package com.google.sps.servlets.second;

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

@WebServlet("/add-info")
public class AddSchoolData extends HttpServlet {

  private Boolean isNewSchool = true;
  private Boolean isNewCourse = true;
  private Boolean isNewProfessor = true;

  private Key existingSchoolKey;
  private Key existingCourseKey;
  private Key existingProfessorKey;

  private DatastoreService db = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    addSchoolData(db, request);
    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.sendRedirect("/addSchoolInfo.html");
  }

  public List<Entity> findQueryMatch(
      DatastoreService db, String entityType, String entityProperty, String propertyValue) {
    Filter filter = new FilterPredicate(entityProperty, FilterOperator.EQUAL, propertyValue);
    Query q = new Query(entityType).setFilter(filter);
    List<Entity> result = db.prepare(q).asList(FetchOptions.Builder.withDefaults());
    return result;
  }

  public void isNewFlagSwitcher(
      DatastoreService db, String schoolName, String courseName, String profName) {
    List<Entity> qS = findQueryMatch(db, "school", "school-name", schoolName);
    if (!qS.isEmpty()) {
      existingSchoolKey = qS.get(0).getKey();
      isNewSchool = false;
    }

    List<Entity> qC = findQueryMatch(db, "course", "course-name", courseName);
    if (!qC.isEmpty()) {
      existingSchoolKey = qC.get(0).getKey();
      isNewCourse = false;
    }

    List<Entity> qP = findQueryMatch(db, "professor", "professor-name", profName);
    if (!qP.isEmpty()) {
      existingSchoolKey = qP.get(0).getKey();
      isNewProfessor = false;
    }
  }

  public Entity createSchool(DatastoreService db, String schoolName) {
    Entity newSchool = new Entity("school");
    newSchool.setProperty("school-name", schoolName);
    db.put(newSchool);
    return newSchool;
  }

  public Entity createCouse(DatastoreService db, String name, Long units, Key parent) {
    Entity newCourse = new Entity("course", parent);
    newCourse.setProperty("course-name", name);
    newCourse.setProperty("units", units);
    db.put(newCourse);
    return newCourse;
  }

  public Entity createProfessor(DatastoreService db, String name, Key parent) {
    Entity newProfessor = new Entity("professor", parent);
    newProfessor.setProperty("professor-name", name);
    db.put(newProfessor);
    return newProfessor;
  }

  public Entity createTerm(DatastoreService db, String term, Key professor, Key parent) {
    Entity newTerm = new Entity("term", parent);
    newTerm.setProperty("term", term);
    newTerm.setProperty("professor", professor);
    db.put(newTerm);
    return newTerm;
  }

  public void addSchoolData(DatastoreService db, HttpServletRequest request) {
    String schoolName = request.getParameter("school-name");
    String courseName = request.getParameter("course-name");
    String termName = request.getParameter("term");
    Long units = Long.parseLong(request.getParameter("units"));
    String profName = request.getParameter("professor-name");

    isNewFlagSwitcher(db, schoolName, courseName, profName);

    if (isNewSchool) {
      Entity school = createSchool(db, schoolName);
      Entity course = createCouse(db, courseName, units, school.getKey());
      Entity professor = createProfessor(db, profName, school.getKey());
      Entity term = createTerm(db, termName, professor.getKey(), course.getKey());
    } else {
      if (isNewCourse) {
        if (isNewProfessor) {
          Entity course = createCouse(db, courseName, units, existingSchoolKey);
          Entity professor = createProfessor(db, profName, existingSchoolKey);
          Entity term = createTerm(db, termName, professor.getKey(), course.getKey());
        } else {
          Entity course = createCouse(db, courseName, units, existingSchoolKey);
          Entity term = createTerm(db, termName, existingProfessorKey, course.getKey());
        }
      } else {
        if (isNewProfessor) {
          Entity professor = createProfessor(db, profName, existingSchoolKey);
          Entity term = createTerm(db, termName, professor.getKey(), existingCourseKey);
        } else {
          Entity term = createTerm(db, termName, existingProfessorKey, existingCourseKey);
        }
      }
    }
  }
}
