package dev.sfafy.pinchat;

import java.util.ArrayList;
import java.util.List;

public class MessageGroup {
  public String name;
  public int x;
  public int y;
  public double scale;
  public List<String> messages;

  public MessageGroup(String name, int x, int y, double scale) {
    this.name = name;
    this.x = x;
    this.y = y;
    this.scale = scale;
    this.messages = new ArrayList<>();
  }
}
