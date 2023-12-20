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

  int size;
  Intersection[][] board;
  ArrayList<Chain> chains = new ArrayList<>();
  private int maxChainId = 0;

  public Board(int size) {
    this.size = size;
    this.board =
        new Intersection[size][size]; // by default every cell is 0 = EMPTY
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
  public Board randomize() {
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
  public void flip(int x, int y) {
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
   *     coordinates x,
   *     y
   */
  int getValue(int x, int y) { return board[x][y].getState(); }

  void putBlack(int x, int y) {
    if (correctMove(x, y, BLACK)) {
      board[x][y].setState(BLACK);
    }
  }

  void putWhite(int x, int y) {
    if (correctMove(x, y, WHITE)) {
      board[x][y].setState(WHITE);
    }
  }

  public void putStone(int x, int y, int playerColor) {
    if (correctMove(x, y, playerColor)) {
      board[x][y].setState(playerColor);
      // board[x][y].takeLiberties();
      ArrayList<Integer> ids = board[x][y].findChain();
      if (!(ids.isEmpty())) {
        for (int i = 0; i < ids.size(); ++i) {
          if (i != 0) {
            changeChain(ids.get(i), ids.get(0));
            removeChain(ids.get(i));
          } else {
            board[x][y].chainId = ids.get(0);
          }
        }
      } else {
        ArrayList<Intersection> toGain = board[x][y].gainToChain();
        Chain newChain = createChain(playerColor);
        for (Intersection i : toGain) {
          newChain.addOne(i);
        }
      }
    }
  }

  private Chain createChain(int color) {
    Chain newChain = new Chain(color, maxChainId + 1);
    chains.add(newChain);
    return newChain;
  }

  private void changeChain(int currentId, int newId) {
    for (Chain ch : chains) {
      if (ch.id == currentId) {
        ch.changeId(newId);
      }
    }
  }

  private void removeChain(int id) {
    for (Chain ch : chains) {
      if (ch.id == id) {
        chains.remove(ch);
      }
    }
  }

  // it won't work for chains. we need something like chianId for each
  // Intersection. I need to think more about
  void removeStone(int x, int y) {
    board[x][y].setState(FREE);
    board[x][y].returnLiberties();
  }

  void removeCaptured() {
    for (int i = 0; i != size; ++i) {
      for (int j = 0; j != size; ++j) {
        if (board[i][j].getLiberty() <= 0) {
          board[i][j].removeChain();
        }
      }
    }
  }

  /**
   * Checks if the new stone can be put on the intersetion.
   * @param state FREE, BLACK, WHITE
   * @return true if the intersetion is FREE = 0
   */
  boolean validMove(int state) { return (state == FREE); }

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
  public void setNeighbours() {
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
