package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

public class GraphDataObject {
  List<Object> hourList = new ArrayList<>();
  List<Object> difficultyList = new ArrayList<>();

  public GraphDataObject(String hourTitle, String diffTitle) {
    List<String> titleContainer = new ArrayList();
    titleContainer.add(hourTitle);
    hourList.add(titleContainer);

    titleContainer.removeAll(titleContainer);
    titleContainer.add(diffTitle);
    difficultyList.add(titleContainer);
  }

  public void addHour(String newHour) {
    List<Integer> hourContainer = new ArrayList();
    hourContainer.add(Integer.parseInt(newHour));
    hourList.add(hourContainer);
  }

  public void addDifficulty(String newDifficulty) {
    List<Integer> difficultyContainer = new ArrayList();
    difficultyContainer.add(Integer.parseInt(newDifficulty));
    difficultyList.add(difficultyContainer);
  }

  public List<Object> getHours() {
    return hourList;
  }

  public List<Object> getDifficulty() {
    return difficultyList;
  }
}
