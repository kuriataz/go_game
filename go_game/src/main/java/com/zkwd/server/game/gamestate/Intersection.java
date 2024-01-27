package com.zkwd.server.game.gamestate;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The container
 */
public class Intersection {

  public static final int FREE = 0;
  private int state;
  private int liberty = 0; // is set by setNeighbours in Board's constructor
  private int chainId = 0;
  public boolean capturing = false;
  public ArrayList<Intersection> neighbours = new ArrayList<Intersection>();

  public Intersection(int state) { this.state = state; }

  public void setState(int state) { this.state = state; }
  public int getState() { return this.state; }

  public int getLiberty() { return this.liberty; }
  public void setLiberty(int liberty) { this.liberty = liberty; }
  public void addLiberty() { ++this.liberty; }
  public void subLiberty() { --this.liberty; }

  public int getChainId() { return this.chainId; }
  public void setChainId(int chainId) { this.chainId = chainId; }

  // when a stone is removed, neighbours' liberties must be updated
  public void returnLiberties() {
    for (Intersection i : neighbours) {
      i.addLiberty();
    }
  }
  // when a stone is added, neighbours' liberties must be updated
  public void takeLiberties() {
    for (Intersection i : neighbours) {
      i.subLiberty();
    }
  }

  public void updateLiberty() {
    this.liberty = 0;
    for (Intersection i : neighbours) {
      if (i.getState() == 0) {
        ++this.liberty;
      }
    }
  }

  /**
   * Checks if newly put stone becomes a part of any existing chain
   * @return ids - ArrayList of ids of chains that Intersection can join in
   *     ascending order
   */
  public ArrayList<Integer> findChain() {
    ArrayList<Integer> ids = new ArrayList<Integer>();
    for (Intersection i : neighbours) {
      if (i.getState() == this.state && i.chainId != 0) {
        if (!(ids.contains(i.chainId)))
          ids.add(i.chainId);
      }
    }
    Collections.sort(ids);
    return ids;
  }

  /**
   * Checks if there is any lonley (not in chain) stone in this.neighbours
   * @return ArrayList of intersections' taken by the lonley stone
   */
  ArrayList<Intersection> gainToChain() {
    ArrayList<Intersection> toGain = new ArrayList<Intersection>();
    for (Intersection i : neighbours) {
      if (i.getState() == this.state && i.chainId == 0) {
        toGain.add(i);
      }
    }
    return toGain;
  }
}
