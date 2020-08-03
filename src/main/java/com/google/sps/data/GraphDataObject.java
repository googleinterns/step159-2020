package com.google.sps.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphDataObject {
  public List<Object> hourList = new ArrayList<>();
  public List<Object> difficultyList = new ArrayList<>();

  public GraphDataObject(String hourTitle, String diffTitle) {
    hourList.add(Arrays.asList(hourTitle));
    difficultyList.add(Arrays.asList(diffTitle));
  }

  public void addHour(String newHour) {
    try {
      hourList.add(Arrays.asList(Integer.parseInt(newHour)));
    } catch (NumberFormatException e) {
      System.out.println("An Integer Was Not Passed: " + newHour);
    }
  }

  public void addDifficulty(String newDifficulty) {
    try {
      difficultyList.add(Arrays.asList(Integer.parseInt(newDifficulty)));
    } catch (NumberFormatException e) {
      System.out.println("An Integer Was Not Passed: " + newDifficulty);
    }
  }

  public List<Object> getHours() {
    return hourList;
  }

  public List<Object> getDifficulty() {
    return difficultyList;
  }
}
