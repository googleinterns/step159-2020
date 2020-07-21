package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

public class EntryObject {
  List<Object> hourList = new ArrayList<>();
  List<Object> difficultyList = new ArrayList<>();
  List<String> container = new ArrayList();

  public EntryObject(String hourTitle, String diffTitle) {
    List<String> container = new ArrayList();
    container.add(hourTitle);
    hourList.add(container);

    container.removeAll(container);
    container.add(hourTitle);
    difficultyList.add(container);
  }

  public void addTitle(String title) {
    List<String> container = new ArrayList();
    container.add(title);
    hourList.add(container);
  }

  public void addHour(String newHour) {
    List<Integer> container = new ArrayList();
    container.add(Integer.parseInt(newHour));
    hourList.add(container);
  }

  public void addDifficulty(String newDifficulty) {
    List<Integer> container = new ArrayList();
    container.add(Integer.parseInt(newDifficulty));
    difficultyList.add(container);
  }

  public List<Object> getHours() {
    return hourList;
  }

  public List<Object> getDifficulty() {
    return difficultyList;
  }
}
