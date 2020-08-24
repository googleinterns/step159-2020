package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

public class ProfDataForTerm {
  String term;
  List<Object> hoursList = new ArrayList();
  List<Object> difficultyList = new ArrayList();
  List<Object> perceptionList = new ArrayList();
  List<Object> commentsList = new ArrayList();

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
