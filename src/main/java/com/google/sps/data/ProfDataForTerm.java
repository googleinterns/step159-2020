package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

public class ProfDataForTerm {
  String course;
  String term;
  String termKey;
  String courseKey;
  List<Object> hoursList = new ArrayList();
  List<Object> difficultyList = new ArrayList();
  List<Object> perceptionList = new ArrayList();
  List<Object> commentsList = new ArrayList();

  public void setTermKey(String newTermKey) {
    termKey = newTermKey;
  }

  public void setCourseKey(String newCourseKey) {
    courseKey = newCourseKey;
  }

  public void setHoursList(List<Object> newHourList) {
    hoursList = newHourList;
  }

  public void setDifficultyList(List<Object> newDifficultyList) {
    difficultyList = newDifficultyList;
  }

  public void setPerceptionList(List<Object> newTermPerceptionList) {
    perceptionList = newTermPerceptionList;
  }

  public void setCommentsList(List<Object> newProfessorCommentsList) {
    commentsList = newProfessorCommentsList;
  }

  public void setTerm(String termName) {
    term = termName;
  }

  public void setCourse(String courseName) {
    course = courseName;
  }

  public String getTermKey() {
    return termKey;
  }

  public String getCourseKey() {
    return courseKey;
  }

  public String getCourse() {
    return course;
  }

  public String getTerm() {
    return term;
  }

  public List<Object> getHoursList() {
    return hoursList;
  }

  public List<Object> getDifficultyList() {
    return difficultyList;
  }

  public List<Object> getPerceptionList() {
    return perceptionList;
  }

  public List<Object> getCommentsList() {
    return commentsList;
  }
}
