package org.uppaal.cli.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestMain {
  public static void main(String[] args) {

Result result = JUnitCore.runClasses(ParserTest.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
      failure.getException().printStackTrace();
    }

 result = JUnitCore.runClasses(DocumentTest.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
      failure.getException().printStackTrace();
    }

 result = JUnitCore.runClasses(TemplateTest.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
      failure.getException().printStackTrace();
    }

 result = JUnitCore.runClasses(LocationTest.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
      failure.getException().printStackTrace();
    }

 result = JUnitCore.runClasses(EdgeTest.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
      failure.getException().printStackTrace();
    }
  }
}