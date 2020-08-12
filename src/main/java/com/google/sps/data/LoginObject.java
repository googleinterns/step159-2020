package com.google.sps.data;

public class LoginObject {
  String id;
  Boolean success;
  String error;

  public LoginObject(String idValue, Boolean successValue, String errorMessage) {
    id = idValue;
    success = successValue;
    error = errorMessage;
  }

  public String getId() {
    return id;
  }

  public Boolean getSuccess() {
    return success;
  }

  public String getError() {
    return error;
  }
}
