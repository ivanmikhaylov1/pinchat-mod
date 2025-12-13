package dev.sfafy.pinchat;

import java.util.ArrayList;
import java.util.List;

public class MessageGroup {
  public String name;
  public int x;
  public int y;
  public double scale;
  public boolean isCollapsed = false;
  public List<String> messages;

  public MessageGroup(String name, int x, int y, double scale) {
    this.name = name;
    this.x = x;
    this.y = y;
    this.scale = scale;
    this.messages = new ArrayList<>();
  }

  public MessageGroup copy() {
    MessageGroup copy = new MessageGroup(this.name, this.x, this.y, this.scale);
    copy.isCollapsed = this.isCollapsed;
    copy.messages.addAll(this.messages);
    return copy;
  }

  @Override
  public String toString() {
    return "MessageGroup{" +
        "name='" + name + '\'' +
        ", x=" + x +
        ", y=" + y +
        ", scale=" + scale +
        ", isCollapsed=" + isCollapsed +
        ", messagesCount=" + messages.size() +
        '}';
  }
}
