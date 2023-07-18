package com.meineAngebote.company;

public class CompanyNotFoundException extends RuntimeException {

  public CompanyNotFoundException(String message) {
    super(message);
  }
}