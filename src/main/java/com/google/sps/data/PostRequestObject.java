package com.google.sps.data;

public class PostRequestObject {
  String school;
  String course;
  String term;
  String units;
  String professor;

  public PostRequestObject(
      String newSchool, String newCourse, String newTerm, String newUnits, String newProfessor) {
    school = newSchool;
    course = newCourse;
    term = newTerm;
    units = newUnits;
    professor = newProfessor;
  }

  public String getSchool() {
    return school;
  }

  public String getCourse() {
    return course;
  }

  public String getTerm() {
    return term;
  }

  public String getUnits() {
    return units;
  }

  public String getProf() {
    return professor;
  }
}
