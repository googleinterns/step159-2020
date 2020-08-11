package com.google.sps.data;

public class LoginObject {
  String id;
  Boolean success;

  public LoginObject(String idValue, Boolean successValue) {
    id = idValue;
    success = successValue;
  }

  public String getId() {
    return id;
  }

  public Boolean getSuccess() {
    return success;
  }
}
