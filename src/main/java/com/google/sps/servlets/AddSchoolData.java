package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreFailureException;
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
  // these are not reset on post call
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

  public void addSchoolData(DatastoreService db, HttpServletRequest request) {
    existingCourseKey = null;
    existingCourseKey = null;
    existingProfessorKey = null;

    String schoolName = request.getParameter("school-name");
    String courseName = request.getParameter("course-name");
    String termName = request.getParameter("term");
    String profName = request.getParameter("prof-name");
    Long units = Long.parseLong(request.getParameter("num-units"));

    Boolean isNewSchool = isNewSchoolDetector(schoolName);
    Boolean isNewCourse = isNewCourseDetector(courseName);
    Boolean isNewProfessor = isNewProfessorDetector(profName);

    Entity school = null;
    Entity course = null;
    Entity professor = null;
    Entity term = null;

    if (isNewSchool) {
      school = createSchool(schoolName);
      course = createCourse(courseName, units, school.getKey());
      professor = createProfessor(profName, school.getKey());
      term = createTerm(termName, professor.getKey(), course.getKey());
    } else {
      if (isNewCourse) {
        if (isNewProfessor) {
          course = createCourse(courseName, units, existingSchoolKey);
          professor = createProfessor(profName, existingSchoolKey);
          term = createTerm(termName, professor.getKey(), course.getKey());
        } else {
          course = createCourse(courseName, units, existingSchoolKey);
          term = createTerm(termName, existingProfessorKey, course.getKey());
        }
      } else {
        if (isNewProfessor) {
          professor = createProfessor(profName, existingSchoolKey);
          term = createTerm(termName, professor.getKey(), existingCourseKey);
        } else {
          term = createTerm(termName, existingProfessorKey, existingCourseKey);
        }
      }
    }
    putNewEntities(school, course, term, professor);
  }

  private List<Entity> findQueryMatch(
      String entityType, String entityProperty, String propertyValue) {
    Filter filter = new FilterPredicate(entityProperty, FilterOperator.EQUAL, propertyValue);
    Query q = new Query(entityType).setFilter(filter);
    List<Entity> result = db.prepare(q).asList(FetchOptions.Builder.withDefaults());
    return result;
  }

  private Boolean isNewSchoolDetector(String schoolName) {
    List<Entity> querySchool = findQueryMatch("School", "school-name", schoolName);
    if (!querySchool.isEmpty()) {
      existingSchoolKey = querySchool.get(0).getKey();
      return false;
    }
    return true;
  }

  private Boolean isNewCourseDetector(String courseName) {
    List<Entity> queryCourse = findQueryMatch("Course", "course-name", courseName);
    if (!queryCourse.isEmpty()) {
      existingCourseKey = queryCourse.get(0).getKey();
      return false;
    }
    return true;
  }

  private Boolean isNewProfessorDetector(String profName) {
    List<Entity> queryProfessor = findQueryMatch("Professor", "professor-name", profName);
    if (!queryProfessor.isEmpty()) {
      existingProfessorKey = queryProfessor.get(0).getKey();
      return false;
    }
    return true;
  }

  private Entity createSchool(String schoolName) {
    Entity newSchool = new Entity("School");
    newSchool.setProperty("school-name", schoolName);
    return newSchool;
  }

  private Entity createCourse(String name, Long units, Key parent) {
    Entity newCourse = new Entity("Course", parent);
    newCourse.setProperty("course-name", name);
    newCourse.setProperty("units", units);
    return newCourse;
  }

  private Entity createProfessor(String name, Key parent) {
    Entity newProfessor = new Entity("Professor", parent);
    newProfessor.setProperty("professor-name", name);
    return newProfessor;
  }

  private Entity createTerm(String term, Key professor, Key parent) {
    Entity newTerm = new Entity("Term", parent);
    newTerm.setProperty("term", term);
    newTerm.setProperty("professorKey", professor);
    return newTerm;
  }

  private void putNewEntities(Entity school, Entity course, Entity term, Entity professor)
      throws DatastoreFailureException, IllegalArgumentException {
    try {
      if (school != null) {
        db.put(school);
      }
      if (course != null) {
        db.put(course);
      }
      if (professor != null) {
        db.put(professor);
      }
      if (term != null) {
        db.put(term);
      }
    } catch (DatastoreFailureException | IllegalArgumentException e) {
      throw e;
    }
  }
}
