package com.google.sps.data;

public class Course {
  String name;
  String professor;
  Long units;
  String term;
  String school;

  public Course(
      String courseName, String professorName, Long numUnits, String termName, String schoolName) {
    name = courseName;
    professor = professorName;
    units = numUnits;
    term = termName;
    school = schoolName;
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
}
