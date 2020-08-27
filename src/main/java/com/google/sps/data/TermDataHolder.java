package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

public class TermDataHolder {
  List<Object> hoursList = new ArrayList();
  List<Object> difficultyList = new ArrayList();
  List<Object> termPerceptionList = new ArrayList();
  List<Object> termScoreList = new ArrayList();
  List<Object> professorPreceptionList = new ArrayList();
  List<Object> professorScoreList = new ArrayList();
  List<Object> termCommentsList = new ArrayList();
  List<Object> professorCommentsList = new ArrayList();
  List<Object> gradesList = new ArrayList();

  public void setHoursList(List<Object> newHourList) {
    hoursList = newHourList;
  }

  public void setDifficultyList(List<Object> newDifficultyList) {
    difficultyList = newDifficultyList;
  }

  public void setTermPerceptionList(List<Object> newTermPerceptionList) {
    termPerceptionList = newTermPerceptionList;
  }

  public void setTermScoreList(List<Object> newTermScoreList) {
    termScoreList = newTermScoreList;
  }

  public void setProfessorPerceptionList(List<Object> newProfessorPerceptionList) {
    professorPreceptionList = newProfessorPerceptionList;
  }

  public void setProfessorScoreList(List<Object> newProfessorScoreList) {
    professorScoreList = newProfessorScoreList;
  }

  public void setTermCommentsList(List<Object> newTermCommentsList) {
    termCommentsList = newTermCommentsList;
  }

  public void setProfessorCommentsList(List<Object> newProfessorCommentsList) {
    professorCommentsList = newProfessorCommentsList;
  }

  public void setGradesList(List<Object> newGradesList) {
    gradesList = newGradesList;
  }

  public List<Object> getGradesList() {
    return gradesList;
  }

  public List<Object> getHoursList() {
    return hoursList;
  }

  public List<Object> getDifficultyList() {
    return difficultyList;
  }

  public List<Object> getTermPerceptionList() {
    return termPerceptionList;
  }

  public List<Object> getTermScoreList() {
    return termScoreList;
  }

  public List<Object> getProfessorPerceptionList() {
    return professorPreceptionList;
  }

  public List<Object> getProfessorScoreList() {
    return professorScoreList;
  }

  public List<Object> getTermCommentsList() {
    return termCommentsList;
  }

  public List<Object> getProfessorCommentsList() {
    return professorCommentsList;
  }
}
