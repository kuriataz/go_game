package com.zkwd.server.game.gamestate;

import java.util.ArrayList;

/**
 * The container
 */
public class Intersection {
  private int state;
  private Boolean breath = true;
  public ArrayList<Intersection> neighbours = new ArrayList<Intersection>();

  Intersection(int state) { this.state = state; }

  void setState(int state) { this.state = state; }

  int getState() { return this.state; }

  Boolean checkBreath() { return this.breath; }

  /**
   *
   * @return true if the intersection (or it's chain has at least one breath).
   *     False if not
   */
  Boolean isBreathing() {
    if (this.state == 0) {
      this.breath = true;
      return true;
    }
    for (Intersection i : neighbours) {
      if (i.getState() == 0) {
        this.breath = true;
        return true;
      }
      if (i.getState() == this.state) {
        return i.isBreathing();
      }
    }
    return false;
  }
}
