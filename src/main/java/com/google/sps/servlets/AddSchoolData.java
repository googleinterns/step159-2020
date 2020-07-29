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
    response.sendRedirect("/AddSchoolData.html");
  }

  public List<Entity> findQueryMatch(
      String entityType, String entityProperty, String propertyValue) {
    Filter filter = new FilterPredicate(entityProperty, FilterOperator.EQUAL, propertyValue);
    Query q = new Query(entityType).setFilter(filter);
    List<Entity> result = db.prepare(q).asList(FetchOptions.Builder.withDefaults());
    return result;
  }

  public void isNewEntityDetector(String schoolName, String courseName, String profName) {
    List<Entity> querySchool = findQueryMatch("School", "school-name", schoolName);
    if (!querySchool.isEmpty()) {
      existingSchoolKey = querySchool.get(0).getKey();
      isNewSchool = false;
    }

    List<Entity> queryCourse = findQueryMatch("Course", "course-name", courseName);
    if (!queryCourse.isEmpty()) {
      existingSchoolKey = queryCourse.get(0).getKey();
      isNewCourse = false;
    }

    List<Entity> queryProfessor = findQueryMatch("Professor", "professor-name", profName);
    if (!queryProfessor.isEmpty()) {
      existingSchoolKey = queryProfessor.get(0).getKey();
      isNewProfessor = false;
    }
  }

  public Entity createSchool(String schoolName) {
    Entity newSchool = new Entity("School");
    newSchool.setProperty("school-name", schoolName);
    db.put(newSchool);
    return newSchool;
  }

  public Entity createCourse(String name, Long units, Key parent) {
    Entity newCourse = new Entity("Course", parent);
    newCourse.setProperty("course-name", name);
    newCourse.setProperty("units", units);
    db.put(newCourse);
    return newCourse;
  }

  public Entity createProfessor(String name, Key parent) {
    Entity newProfessor = new Entity("Professor", parent);
    newProfessor.setProperty("professor-name", name);
    db.put(newProfessor);
    return newProfessor;
  }

  public Entity createTerm(String term, Key professor, Key parent) {
    Entity newTerm = new Entity("Term", parent);
    newTerm.setProperty("term", term);
    newTerm.setProperty("professorKey", professor);
    db.put(newTerm);
    return newTerm;
  }

  public void addSchoolData(DatastoreService db, HttpServletRequest request) {
    String schoolName = request.getParameter("school-name");
    String courseName = request.getParameter("course-name");
    String termName = request.getParameter("term");
    String profName = request.getParameter("professor-name");
    Long units = Long.parseLong(request.getParameter("units"));

    isNewEntityDetector(schoolName, courseName, profName);

    if (isNewSchool) {
      Entity school = createSchool(schoolName);
      Entity course = createCourse(courseName, units, school.getKey());
      Entity professor = createProfessor(profName, school.getKey());
      Entity term = createTerm(termName, professor.getKey(), course.getKey());
    } else {
      if (isNewCourse) {
        if (isNewProfessor) {
          Entity course = createCourse(courseName, units, existingSchoolKey);
          Entity professor = createProfessor(profName, existingSchoolKey);
          Entity term = createTerm(termName, professor.getKey(), course.getKey());
        } else {
          Entity course = createCourse(courseName, units, existingSchoolKey);
          Entity term = createTerm(termName, existingProfessorKey, course.getKey());
        }
      } else {
        if (isNewProfessor) {
          Entity professor = createProfessor(profName, existingSchoolKey);
          Entity term = createTerm(termName, professor.getKey(), existingCourseKey);
        } else {
          Entity term = createTerm(termName, existingProfessorKey, existingCourseKey);
        }
      }
    }
  }
}
