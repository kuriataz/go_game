package com.zkwd.server.game.gamestate;

import java.util.ArrayList;

public class Chain {
  public static final int FREE = 0;
  private int liberty = 0;
  public ArrayList<Intersection> chain = new ArrayList<Intersection>();
  private int id;

  public Chain(int id) { this.id = id; }

  public int getLiberty() { return this.liberty; }
  public int getId() { return this.id; }

  public void changeId(int newId) {
    this.id = newId;
    for (Intersection i : chain) {
      i.setChainId(newId);
    }
  }

  public void removeStones() {
    for (Intersection i : chain) {
      i.setState(FREE);
      i.returnLiberties();
      i.setChainId(0);
    }
  }

  public void updateLiberty() {
    this.liberty = 0;
    for (Intersection i : chain) {
      this.liberty += i.getLiberty();
    }
  }

  public void addOne(Intersection next) {
    this.chain.add(next);
    next.setChainId(this.id);

    updateLiberty();
  }

  public void reset() {
    for (Intersection i : chain) {
      i.setChainId(0);
    }
    chain.clear();
  }
}
