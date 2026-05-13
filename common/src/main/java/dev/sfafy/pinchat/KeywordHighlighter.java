package dev.sfafy.pinchat;

import java.util.List;

public class KeywordHighlighter {

  public static boolean containsKeyword(String message, List<String> keywords) {
    if (keywords.isEmpty()) return false;
    String lower = message.toLowerCase();
    for (String keyword : keywords) {
      if (!keyword.isBlank() && lower.contains(keyword.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Wraps each keyword occurrence in §e...§r (yellow) while preserving surrounding text.
   * Applies keywords in order, longest first to avoid partial overlaps.
   */
  public static String highlight(String message, List<String> keywords) {
    if (keywords.isEmpty()) return message;

    List<String> sorted = keywords.stream()
        .filter(k -> !k.isBlank())
        .sorted((a, b) -> b.length() - a.length())
        .toList();

    String result = message;
    for (String keyword : sorted) {
      result = replaceIgnoreCase(result, keyword, "§e" + keyword + "§r");
    }
    return result;
  }

  private static String replaceIgnoreCase(String text, String target, String replacement) {
    if (target.isEmpty()) return text;
    StringBuilder sb = new StringBuilder();
    String lower = text.toLowerCase();
    String lowerTarget = target.toLowerCase();
    int start = 0;
    int idx;
    while ((idx = lower.indexOf(lowerTarget, start)) != -1) {
      sb.append(text, start, idx);
      sb.append(replacement);
      start = idx + target.length();
    }
    sb.append(text, start, text.length());
    return sb.toString();
  }
}
