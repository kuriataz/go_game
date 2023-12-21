package com.zkwd.server.game;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * The container
 */
public class Intersection {

  public static final int FREE = 0;
  private int state;
  private int liberty = 0; // is set by setNeighbours in Board's constructor
  public int chainId = 0;
  public ArrayList<Intersection> neighbours = new ArrayList<Intersection>();

  Intersection(int state) { this.state = state; }

  void setState(int state) { this.state = state; }

  int getState() { return this.state; }

  int getLiberty() { return this.liberty; }
  void setLiberty(int liberty) { this.liberty = liberty; }

  void addLiberty() { ++this.liberty; }
  void subLiberty() { --this.liberty; }

  // when a stone is removed, neighbours' liberties must be updated
  void returnLiberties() {
    for (Intersection i : neighbours) {
      i.addLiberty();
    }
  }
  // when a stone is added, neighbours' liberties must be updated
  void takeLiberties() {
    for (Intersection i : neighbours) {
      i.subLiberty();
    }
  }

  void removeChain() {
    for (Intersection i : neighbours) {
      if (i.getState() == this.state) {
        i.removeChain();
      }
      i.setState(FREE);
      i.returnLiberties();
    }
  }

  /**
   * Checks if newly put stone becomes a part of any existing chain
   * @return ids - ArrayList of ids of chains that Intersection can join in
   *     ascending order
   */
  ArrayList<Integer> findChain() {
    ArrayList<Integer> ids = new ArrayList<Integer>();
    for (Intersection i : neighbours) {
      if (i.getState() == this.state && i.chainId != 0) {
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
