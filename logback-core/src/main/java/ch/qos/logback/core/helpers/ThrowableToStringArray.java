/**
 * LOGBack: the reliable, fast and flexible logging library for Java.
 * 
 * Copyright (C) 1999-2005, QOS.ch
 * 
 * This library is free software, you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation.
 */
package ch.qos.logback.core.helpers;

import ch.qos.logback.core.CoreGlobal;


public class ThrowableToStringArray {

  public static String[] extractStringRep(Throwable t, StackTraceElement[] parentSTE) {
    String[] result;

    StackTraceElement[] ste = t.getStackTrace();
    final int numberOfcommonFrames = findNumberOfCommonFrames(ste, parentSTE);

    final String[] firstArray;
    if (numberOfcommonFrames == 0) {
      firstArray = new String[ste.length + 1];
    } else {
      firstArray = new String[ste.length - numberOfcommonFrames + 2];
    }

    firstArray[0] = formatFirstLine(t, parentSTE);
    for (int i = 0; i < (ste.length - numberOfcommonFrames); i++) {
      firstArray[i + 1] = ste[i].toString();
    }

    if (numberOfcommonFrames != 0) {
      firstArray[firstArray.length - 1] = numberOfcommonFrames
          + " common frames omitted";
    }

    Throwable cause = t.getCause();
    if (cause != null) {
      final String[] causeArray = ThrowableToStringArray.extractStringRep(cause, ste);
      String[] tmp = new String[firstArray.length + causeArray.length];
      System.arraycopy(firstArray, 0, tmp, 0, firstArray.length);
      System
          .arraycopy(causeArray, 0, tmp, firstArray.length, causeArray.length);
      result = tmp;
    } else {
      result = firstArray;
    }
    return result;
  }
  
  private static String formatFirstLine(Throwable t, StackTraceElement[] parentSTE) {
    String prefix = "";
    if (parentSTE != null) {
      prefix = CoreGlobal.CAUSED_BY;
    }

    String result = prefix + t.getClass().getName();
    if (t.getMessage() != null) {
      result += ": " + t.getMessage();
    }
    return result;
  }
  
  private static int findNumberOfCommonFrames(StackTraceElement[] ste,
      StackTraceElement[] parentSTE) {
    if (parentSTE == null) {
      return 0;
    }

    int steIndex = ste.length - 1;
    int parentIndex = parentSTE.length - 1;
    int count = 0;
    while (steIndex >= 0 && parentIndex >= 0) {
      if (ste[steIndex].equals(parentSTE[parentIndex])) {
        count++;
      } else {
        break;
      }
      steIndex--;
      parentIndex--;
    }
    return count;
  }

}
