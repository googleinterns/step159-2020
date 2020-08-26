package com.google.sps.data;

import java.util.*;

public class Term {
  String season;
  int year;
  ListIterator termIterator;
  int numTerms;
  int currTerm;
  ArrayList<String> termList;
  Boolean quarter;

  final List<String> QTR_SCHOOLS = Arrays.asList("caltech", "calpoly", "stanford", "berkeley");
  final ArrayList<String> QTR_TERMS =
      new ArrayList<String>(Arrays.asList("Winter", "Spring", "Summer", "Fall"));
  final ArrayList<String> SEMESTER_TERMS =
      new ArrayList<String>(Arrays.asList("Spring", "Summer", "Fall"));

  public Term(String termString, Boolean isQuarter) {
    season = termString.split(" ")[0];
    year = Integer.parseInt(termString.split(" ")[1]);
    quarter = isQuarter;
    if (isQuarter) {
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

  public Term getNext() {
    if (currTerm + 1 < numTerms) {
      return new Term(termList.get(currTerm + 1) + " " + String.valueOf(year), quarter);
    } else {
      return new Term(termList.get(0) + " " + String.valueOf(year + 1), quarter);
    }
  }

  public Term getPrev() {
    if (currTerm == 0) {
      return new Term(termList.get(numTerms - 1) + " " + String.valueOf(year - 1), quarter);
    } else {
      return new Term(termList.get(currTerm - 1) + " " + String.valueOf(year), quarter);
    }
  }

  public Boolean isQuarter() {
    return quarter;
  }

  public String toString() {
    return season + " " + String.valueOf(year);
  }
}
