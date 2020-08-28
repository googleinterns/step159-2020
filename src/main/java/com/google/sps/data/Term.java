package com.google.sps.data;

import java.util.*;

public class Term implements Comparable<Term> {
  String season;
  int year;
  ListIterator termIterator;
  int numTerms;
  int currTerm;
  ArrayList<String> termList;
  Boolean isQuarter;

  final List<String> QTR_SCHOOLS = Arrays.asList("caltech", "calpoly", "stanford", "berkeley");
  final ArrayList<String> QTR_TERMS =
      new ArrayList<String>(Arrays.asList("Winter", "Spring", "Summer", "Fall"));
  final ArrayList<String> SEMESTER_TERMS =
      new ArrayList<String>(Arrays.asList("Spring", "Summer", "Fall"));

  public Term(String termString, Boolean isQuarterSystem) {
    season = termString.split(" ")[0];
    year = Integer.parseInt(termString.split(" ")[1]);
    isQuarter = isQuarterSystem;
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
      return new Term(termList.get(currTerm + 1) + " " + String.valueOf(year), isQuarter);
    } else {
      return new Term(termList.get(0) + " " + String.valueOf(year + 1), isQuarter);
    }
  }

  public Term getPrev() {
    if (currTerm == 0) {
      return new Term(termList.get(numTerms - 1) + " " + String.valueOf(year - 1), isQuarter);
    } else {
      return new Term(termList.get(currTerm - 1) + " " + String.valueOf(year), isQuarter);
    }
  }

  public Boolean isQuarter() {
    return isQuarter;
  }

  public String toString() {
    return season + " " + String.valueOf(year);
  }

  @Override
  public int compareTo(Term term) {
    if (year == term.year) {
      return term.termList.indexOf(term.season) - termList.indexOf(season);
    } else {
      return term.year - year;
    }
  }

  public int compare(Term one, Term two) {
    return one.compareTo(two);
  }
}
