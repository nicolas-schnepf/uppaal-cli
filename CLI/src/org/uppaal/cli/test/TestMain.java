package org.uppaal.cli.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestMain {
  public static void main(String[] args) {

// run the tests for the editor handler

    Result result = JUnitCore.runClasses(EditorHandlerTest.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString()+"\n"+failure.getTrace());
    }
  }
}
