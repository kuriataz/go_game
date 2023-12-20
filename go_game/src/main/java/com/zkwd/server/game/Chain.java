package com.zkwd.server.game;

import java.util.ArrayList;

public class Chain {
  public static final int FREE = 0;
  private int color;
  public ArrayList<Intersection> chain = new ArrayList<Intersection>();
  private int liberty = 0;
  public int id;
  public boolean alive;

  public Chain(int color, int id) {
    this.color = color;
    this.alive = true;
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

  public void addOne(Intersection next) {
    if (checkAddOne(next)) {
      this.chain.add(next);
      next.chainId = this.id;
      this.liberty = this.liberty + next.getLiberty();
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

  // public void addWholeChain(ArrayList<Intersection> newChain) {
  //   if (checkAddWholeChain(newChain)) {
  //     this.chain.addAll(newChain);
  //   }
  // }
  // private boolean checkAddWholeChain(ArrayList<Intersection> newChain) {
  //   if (newChain.get(0).getState() != this.color) {
  //     return false;
  //   }
  //   for (Intersection i : chain) {
  //     for (Intersection j : i.neighbours) {
  //       for (Intersection k : newChain) {
  //         if (j == k) {
  //           return true;
  //         }
  //       }
  //     }
  //   }
  //   return false;
  // }
}
