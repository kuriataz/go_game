package com.zkwd.server.game;

import java.util.ArrayList;
import java.util.Random;

/**
 * Stores game information: the size of the board and placement of pieces on it.
 */
public class Board {

  public static final int BLACK = -1;
  public static final int WHITE = 1;
  public static final int FREE = 0;

  private int size;
  private int maxChainId;
  Intersection[][] board;
  ArrayList<Chain> chains = new ArrayList<Chain>();

  public Board(int size) {
    this.size = size;
    this.maxChainId = 0;
    this.board = new Intersection[size][size]; // by default every cell is 0
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        board[i][j] = new Intersection(0);
      }
    }
    setNeighbours();
  }

  /**
   * Randomizes the board state. Mostly for debug purposes.
   * @return The board that has been randomized (this)
   */
  Board randomize() {
    Random r = new Random();
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        board[i][j].setState(r.nextInt() % 3 - 1);
      }
    }
    return this;
  }

  /**
   * Cycle between values (debug).
   * @param x row
   * @param y column
   */
  void flip(int x, int y) {
    board[y][x].setState((board[x][y].getState() + 2) % 3 - 1);
  }

  /**
   * Gets the size of the board.
   * @return The size of the board
   */
  int getSize() { return this.size; }

  /**
   * Returns the state of the intersection (color).
   * @param x intersection's x coordinate
   * @param y intersection's y coordinate
   * @return state (0: free, 1: white, -1: black) of the intersection with
   *     coordinates x, y
   */
  int getValue(int x, int y) { return board[x][y].getState(); }

  void putStone(int x, int y, int playerColor) {
    if (correctMove(x, y, playerColor)) {
      board[x][y].setState(playerColor);
      board[x][y].takeLiberties();

      // new stone joins existing chain
      ArrayList<Integer> ids = new ArrayList<Integer>();
      ids = board[x][y].findChain();
      if (!(ids.isEmpty())) {
        for (int i = 0; i < ids.size(); ++i) {
          if (i != 0) {
            changeChain(ids.get(i), ids.get(0));
            deleteChain(ids.get(i));
          } else {
            board[x][y].chainId = ids.get(0);
            for (Chain ch : chains) {
              if (ch.id == ids.get(0)) {
                ch.addOne(board[x][y]);
              }
            }
          }
        }
      }
      // gain all lonley stones around newly put stone and try to add them to
      // chain
      ArrayList<Intersection> toGain = board[x][y].gainToChain();
      if (!(toGain.isEmpty())) {
        if (board[x][y].chainId == 0) {
          Chain newChain = createChain(playerColor);
          newChain.addOne(board[x][y]);
          System.out.println("chainId: " + board[x][y].chainId);
          for (Intersection i : toGain) {
            newChain.addOne(i);
          }
        } else if (findChain(board[x][y].chainId) != null) {
          for (Intersection i : toGain) {
            findChain(board[x][y].chainId).addOne(i);
          }
        }
      }
    }
  }

  private Chain findChain(int chainId) {
    for (Chain ch : chains) {
      if (ch.id == chainId) {
        return ch;
      }
    }
    return null;
  }

  private Chain createChain(int color) {
    Chain newChain = new Chain(color, maxChainId + 1);
    ++maxChainId;
    chains.add(newChain);
    return newChain;
  }

  private void changeChain(int currentId, int newId) {
    ArrayList<Intersection> stones = new ArrayList<Intersection>();
    for (Chain ch : chains) {
      if (ch.id == currentId) {
        ch.changeId(newId);
        stones = ch.chain;
      }
    }
    // move stones to new chain
    for (Chain ch : chains) {
      if (ch.id == newId) {
        for (Intersection i : stones) {
          ch.addOne(i);
        }
      }
    }
  }

  private void deleteChain(int id) {
    if (!(chains.isEmpty())) {
      for (Chain ch : chains) {
        if (ch.id == id) {
          chains.remove(ch);
        }
      }
    }
  }

  void removeStone(int x, int y) {
    board[x][y].setState(FREE);
    board[x][y].returnLiberties();
  }

  void removeChain(Chain chain) {
    chain.removeStones();
    deleteChain(chain.id);
  }

  void removeCapturedStones() {
    for (int i = 0; i != size; ++i) {
      for (int j = 0; j != size; ++j) {
        if (board[i][j].getLiberty() <= 0 && board[i][j].chainId == 0) {
          removeStone(i, j);
        }
      }
    }
  }
  void removeCapturedChains() {
    for (Chain ch : chains) {
      ch.updateLiberty();
      if (ch.getLiberty() <= 0) {
        removeChain(ch);
      }
    }
  }

  /**
   * Checks if the new stone can be put on the intersetion.
   * @param x - first coordinate of the intersection
   * @param y - second coordinate of the intersection
   * @param payerColor BLACK, WHITE
   * @return true if the intersetion is FREE and suicide isn't commited
   */
  boolean correctMove(int x, int y, int playerColor) {
    boolean free = (board[x][y].getState() == FREE);
    boolean suicide = true;
    for (Intersection i : board[x][y].neighbours) {
      if (i.getState() != -(playerColor)) {
        suicide = false;
      }
    }
    return free && !suicide;
  }

  /**
   * Sets neighbours of each intersection and gives them numbers of liberties.
   */
  void setNeighbours() {
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (i + 1 < size) {
          board[i][j].neighbours.add(board[i + 1][j]);
          board[i][j].addLiberty();
        }
        if (i - 1 >= 0) {
          board[i][j].neighbours.add(board[i - 1][j]);
          board[i][j].addLiberty();
        }
        if (j + 1 < size) {
          board[i][j].neighbours.add(board[i][j + 1]);
          board[i][j].addLiberty();
        }
        if (j - 1 >= 0) {
          board[i][j].neighbours.add(board[i][j - 1]);
          board[i][j].addLiberty();
        }
      }
    }
  }

  /**
   * FORMATTING OF THE OUTPUT STRING
   * white -> W
   * black -> B
   * empty -> E
   * printed column by column, columns divided by |
   */
  public String prepareBoardString() {
    String out = "";
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        int k = board[i][j].getState();
        out += (k == 1) ? "W" : (k == -1) ? "B" : "E";
      }
      out += "|";
    }
    return out;
  }
}
