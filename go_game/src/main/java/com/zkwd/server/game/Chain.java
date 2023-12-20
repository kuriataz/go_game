package com.zkwd.server.game;

import java.util.ArrayList;

public class Chain {
  private int color;
  private ArrayList<Intersection> chain = new ArrayList<Intersection>();
  public int id;
  public boolean alive;

  public Chain(int color, int id) {
    this.color = color;
    this.alive = true;
    this.id = id;
  }

  public void changeId(int newId) {
    this.id = newId;
    for (Intersection i : chain) {
      i.chainId = newId;
    }
  }

  public void addOne(Intersection next) {
    if (checkAddOne(next)) {
      this.chain.add(next);
      next.chainId = this.id;
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
