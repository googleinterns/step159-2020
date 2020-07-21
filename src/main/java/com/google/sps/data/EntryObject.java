package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

public class EntryObject {
  List<Integer> hourList = new ArrayList<>();
  List<Integer> difficultyList = new ArrayList<>();

  public void addHour(String newHour) {
    hourList.add(Integer.parseInt(newHour));
  }

  public void addDifficulty(String newDifficulty) {
    difficultyList.add(Integer.parseInt(newDifficulty));
  }

  public List<Integer> getHours() {
    return hourList;
  }

  public List<Integer> getDifficulty() {
    return difficultyList;
  }
}
