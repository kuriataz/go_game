package com.zkwd;

import java.util.ArrayList;

public class Intersection {
  private int state;
  private Boolean breath = true;
  private ArrayList<Intersection> neighbours;

  public Intersection(int state) { this.state = state; }

  public void setState(int state) { this.state = state; }

  public int getState() { return this.state; }

  public Boolean checkBreath() { return this.breath; }

  public Boolean isBreathing() {
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
