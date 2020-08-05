package com.google.sps.data;

public class Course {
  String name;
  String professor;
  Long units;
  String term;

  public Course(String courseName, String professorName, Long numUnits, String termName) {
    name = courseName;
    professor = professorName;
    units = numUnits;
    term = termName;
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
}
