package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.util.*;

public class Course implements Comparable<Course> {
  String name;
  String professor;
  Long units;
  String term;
  String school;
  String termKey;
  String courseKey;

  public Course(
      String courseName,
      String professorName,
      Long numUnits,
      String termName,
      String schoolName,
      DatastoreService db) {
    name = courseName;
    professor = professorName;
    units = numUnits;
    term = termName;
    school = schoolName;
    List<Key> keys = findKeys(db, schoolName, courseName, termName, units, professorName);
    termKey = KeyFactory.keyToString(keys.get(0));
    courseKey = KeyFactory.keyToString(keys.get(1));
  }

  public String getName() {
    return name;
  }

  public String getProfessor() {
    return professor;
  }

  public Long getUnits() {
    return units;
  }

  public String getTerm() {
    return term;
  }

  public String getSchool() {
    return school;
  }

  public String getTermKey() {
    return termKey;
  }

  public String getCourseKey() {
    return courseKey;
  }

  @Override
  public int compareTo(Course course) {
    return name.compareTo(course.name);
  }

  public int compare(Course one, Course two) {
    return one.compareTo(two);
  }

  private List<Key> findKeys(
      DatastoreService db,
      String schoolName,
      String courseName,
      String termName,
      Long units,
      String profName) {
    Key schoolKey = findQueryMatch(db, "School", "school-name", schoolName).get(0).getKey();
    List<Filter> filters = new ArrayList();
    Filter courseFilter = new FilterPredicate("course-name", FilterOperator.EQUAL, courseName);
    Filter unitFilter = new FilterPredicate("units", FilterOperator.EQUAL, units);
    filters.add(courseFilter);
    filters.add(unitFilter);

    Query courseQuery =
        new Query("Course").setAncestor(schoolKey).setFilter(CompositeFilterOperator.and(filters));
    Key courseKey =
        db.prepare(courseQuery).asList(FetchOptions.Builder.withDefaults()).get(0).getKey();
    Filter termFilter = new FilterPredicate("term", FilterOperator.EQUAL, termName);
    Query termQuery = new Query("Term").setAncestor(courseKey).setFilter(termFilter);
    Entity foundTerm = db.prepare(termQuery).asList(FetchOptions.Builder.withDefaults()).get(0);
    return Arrays.asList(foundTerm.getKey(), courseKey);
  }

  private List<Entity> findQueryMatch(
      DatastoreService db, String entityType, String entityProperty, String propertyValue) {
    Filter filter = new FilterPredicate(entityProperty, FilterOperator.EQUAL, propertyValue);
    Query q = new Query(entityType).setFilter(filter);
    return db.prepare(q).asList(FetchOptions.Builder.withDefaults());
  }
}
