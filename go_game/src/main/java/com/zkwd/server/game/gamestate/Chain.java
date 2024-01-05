package com.zkwd.server.game.gamestate;

import java.util.ArrayList;

public class Chain {
  private int color;
  private ArrayList<Intersection> chain;

  public Chain(int color, Intersection first) {
    this.color = color;
    this.chain.add(first);
  }

  public void addOne(Intersection next) {
    if (checkAddOne(next)) {
      this.chain.add(next);
    } else {
      // wrong color or place
      // maybe checkAddOne should throw sth
    }
  }
  private boolean checkAddOne(Intersection next) {
    if (next.getState() != this.color) {
      return false;
    }
    for (Intersection i : chain) {
      for (Intersection j : i.neighbours) {
        if (j == next) { // will it work? shouldn't we check coordinates? if so,
                         // this check must be done higher
          return true;
        }
      }
    }
    return false;
  }

  public void addWholeChain(ArrayList<Intersection> newChain) {
    if (checkAddWholeChain(newChain)) {
      this.chain.addAll(newChain);
    }
  }
  private boolean checkAddWholeChain(ArrayList<Intersection> newChain) {
    if (newChain.get(0).getState() != this.color) {
      return false;
    }
    for (Intersection i : chain) {
      for (Intersection j : i.neighbours) {
        for (Intersection k : newChain) {
          if (j == k) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
