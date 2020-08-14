package com.google.sps.data;

import java.util.*;

public class Course implements Comparable<Course> {
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

  @Override
  public int compareTo(Course course) {
    return name.compareTo(course.name);
  }

  public int compare(Course one, Course two) {
    return one.compareTo(two);
    // if(one.name > two.name) {
    //     return 1;
    // } else if (one.name < two.name) {
    //     return -1;
    // } else {
    // return 0;
    // }
  }
}
