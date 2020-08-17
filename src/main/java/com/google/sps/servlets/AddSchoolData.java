package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    Long numEnrolled = Long.parseLong(request.getParameter("num-enrolled"));

    Boolean isNewSchool = isNewSchoolDetector(schoolName);
    Boolean isNewProfessor = isNewProfessorDetector(profName);

    Entity school = null;
    Entity course = null;
    Entity professor = null;
    Entity term = null;

    if (isNewSchool) {
      school = createSchool(schoolName);
      course = createCourse(courseName, units, school.getKey());
      professor = createProfessor(profName, school.getKey());
      term = createTerm(termName, numEnrolled, professor.getKey(), course.getKey());
    } else {
      Boolean isNewCourse = isNewCourseDetector(schoolName, courseName, units);
      if (isNewCourse) {
        if (isNewProfessor) {
          course = createCourse(courseName, units, existingSchoolKey);
          professor = createProfessor(profName, existingSchoolKey);
          term = createTerm(termName, numEnrolled, professor.getKey(), course.getKey());
        } else {
          course = createCourse(courseName, units, existingSchoolKey);
          term = createTerm(termName, numEnrolled, existingProfessorKey, course.getKey());
        }
      } else {
        if (isNewProfessor) {
          professor = createProfessor(profName, existingSchoolKey);
          term = createTerm(termName, numEnrolled, professor.getKey(), existingCourseKey);
        } else {
          term = createTerm(termName, numEnrolled, existingProfessorKey, existingCourseKey);
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

  private List<Entity> findCourseMatch(String schoolName, String courseName, Long units) {
    Key schoolKey = findQueryMatch("School", "school-name", schoolName).get(0).getKey();

    List<Filter> filters = new ArrayList();
    Filter courseFilter = new FilterPredicate("course-name", FilterOperator.EQUAL, courseName);
    Filter unitFilter = new FilterPredicate("units", FilterOperator.EQUAL, units);
    filters.add(courseFilter);
    filters.add(unitFilter);

    Query courseQuery =
        new Query("Course").setAncestor(schoolKey).setFilter(CompositeFilterOperator.and(filters));
    List<Entity> result = db.prepare(courseQuery).asList(FetchOptions.Builder.withDefaults());
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

  private Boolean isNewCourseDetector(String schoolName, String courseName, Long units) {
    List<Entity> queryCourse = findCourseMatch(schoolName, courseName, units);
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

  private Entity createTerm(String term, Long numEnrolled, Key professor, Key parent) {
    Entity newTerm = new Entity("Term", parent);
    newTerm.setProperty("term", term);
    newTerm.setProperty("num-enrolled", enrolled);
    newTerm.setProperty("professorKey", professor);
    newTerm.setProperty("timeStamp", findTermDate(term));
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

  private Date findTermDate(String termName) {
    String[] termList = termName.split(" ");

    int month = 0;
    int termYear = Integer.parseInt(termList[1]);
    if (termList[0].equals("Spring")) {
      month = 2;
    } else if (termList[0].equals("Summer")) {
      month = 5;
    } else if (termList[0].equals("Fall") || termList[0].equals("Autumn")) {
      month = 8;
    } else if (termList[0].equals("Winter")) {
      month = 0;
    } else {
      throw new IllegalArgumentException();
    }

    Calendar startDay = Calendar.getInstance();
    startDay.set(termYear, month, 1);
    Date dateTime = startDay.getTime();

    return dateTime;
  }
}
