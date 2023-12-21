package com.zkwd.server.game;

import java.util.ArrayList;

public class Chain {
  public static final int FREE = 0;
  private int color;
  private int liberty = 0;
  public ArrayList<Intersection> chain = new ArrayList<Intersection>();
  public int id;

  public Chain(int color, int id) {
    this.color = color;
    this.id = id;
  }

  public int getLiberty() { return this.liberty; }

  public void changeId(int newId) {
    this.id = newId;
    for (Intersection i : chain) {
      i.chainId = newId;
    }
  }

  void removeStones() {
    for (Intersection i : chain) {
      i.setState(FREE);
      i.returnLiberties();
      i.chainId = 0;
    }
  }

  void updateLiberty() {
    this.liberty = 0;
    for (Intersection i : chain) {
      this.liberty += i.getLiberty();
    }
  }

  public void addOne(Intersection next) {
    if (checkAddOne(next)) {
      this.chain.add(next);
      next.chainId = this.id;
      this.liberty = this.liberty + next.getLiberty();
    }
  }

  // i think this check isn't neccssary
  private boolean checkAddOne(Intersection next) {
    // if (next.getState() != this.color) {
    //   return false;
    // }
    // for (Intersection i : chain) {
    //   for (Intersection j : i.neighbours) {
    //     if (j == next) { // will it work? shouldn't we check coordinates? if
    //     so,
    //                      // this check must be done higher
    //       return true;
    //     }
    //   }
    // }
    // return false;
    return true;
  }
}
