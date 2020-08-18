package com.google.sps.data;

import java.util.*;

public class Term {
  String season;
  int year;
  String school;
  ListIterator termIterator;
  ArrayList<String> termList;

  final List<String> QTR_SCHOOLS = Arrays.asList("caltech", "calpoly", "stanford", "berkeley");
  final ArrayList<String> QTR_TERMS =
      new ArrayList<String>(Arrays.asList("Winter", "Spring", "Fall"));
  final ArrayList<String> SEMESTER_TERMS = new ArrayList<String>(Arrays.asList("Spring", "Fall"));

  public Term(String termString, String termSchool) {
    season = termString.split(" ")[0];
    year = Integer.parseInt(termString.split(" ")[1]);
    school = termSchool;
    if (QTR_SCHOOLS.contains(school)) {
      termList = QTR_TERMS;
    } else {
      termList = SEMESTER_TERMS;
    }
    System.out.println(termList);
    int currIndex = termList.indexOf(season);
    System.out.println(currIndex);
    termIterator = termList.listIterator(currIndex);
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
    if (termIterator.hasNext()) {
      System.out.println(termIterator.next() + " " + String.valueOf(year));
      return termIterator.next() + " " + String.valueOf(year);
      // return new Term(termToString, school);
    } else {
      System.out.println(termList.get(0) + " " + String.valueOf(year + 1));
      return termList.get(0) + " " + String.valueOf(year + 1);
      // return new Term(termToString, school);
    }
  }

  public String getPrev() {
    if (termIterator.hasPrevious()) {
      return termIterator.previous() + " " + String.valueOf(year);
      // return new Term(termToString, school);
    } else {
      return termList.get(-1) + " " + String.valueOf(year - 1);
      // return new Term(termToString, school);
    }
  }

  public String toString() {
    return season + " " + String.valueOf(year);
  }
}
