package com.google.sps.servlets;

import com.google.appengine.api.datastore.Key;

public class Course {
  String name;
  String professor;
  Long units;
  Key term;

  public Course(String name, String professor, Long units, Key term) {
    name = name;
    professor = professor;
    units = units;
    term = term;
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

  public Key getTerm() {
    return term;
  }
}
