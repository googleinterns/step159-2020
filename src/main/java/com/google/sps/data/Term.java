package com.google.sps.data;

import java.util.*;

public class Term {
  String season;
  int year;
  String school;
  ListIterator termIterator;
  int numTerms;
  int currTerm;
  ArrayList<String> termList;

  final List<String> QTR_SCHOOLS = Arrays.asList("caltech", "calpoly", "stanford", "berkeley");
  final ArrayList<String> QTR_TERMS =
      new ArrayList<String>(Arrays.asList("Winter", "Spring", "Summer", "Fall"));
  final ArrayList<String> SEMESTER_TERMS =
      new ArrayList<String>(Arrays.asList("Spring", "Summer", "Fall"));

  public Term(String termString, String termSchool) {
    season = termString.split(" ")[0];
    year = Integer.parseInt(termString.split(" ")[1]);
    school = termSchool;
    if (QTR_SCHOOLS.contains(school)) {
      termList = QTR_TERMS;
    } else {
      termList = SEMESTER_TERMS;
    }
    numTerms = termList.size();
    currTerm = termList.indexOf(season);
  }

  public String getSeason() {
    return season;
  }

  public int getYear() {
    return year;
  }

  public String getSchool() {
    return school;
  }

  public String getNext() {
    if (currTerm + 1 < numTerms) {
      return termList.get(currTerm + 1) + " " + String.valueOf(year);
    } else {
      return termList.get(0) + " " + String.valueOf(year + 1);
    }
  }

  public String getPrev() {
    if (currTerm == 0) {
      return termList.get(numTerms - 1) + " " + String.valueOf(year - 1);
    } else {
      return termList.get(currTerm - 1) + " " + String.valueOf(year);
    }
  }

  public String toString() {
    return season + " " + String.valueOf(year);
  }
}
