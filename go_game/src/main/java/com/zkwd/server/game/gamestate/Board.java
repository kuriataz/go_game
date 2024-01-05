package com.zkwd.server.game.gamestate;

import java.util.Random;

import com.zkwd.server.game.exceptions.MoveException;

/**
 * Stores game information: the size of the board and placement of pieces on it.
 */
public class Board {

  public static final int BLACK = -1;
  public static final int WHITE = 1;
  public static final int FREE = 0;

  int size;
  Intersection[][] board;

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
   * Randomizes the board state. For debug purposes.
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

  /**
   * Commits the move specified by location and player color, if said move is valid.
   * @param x coordinate
   * @param y coordinate
   * @param playerColor -1 for black, 1 for white
   * @throws MoveException if the specified move is invalid.
   */
  public void putStone(int x, int y, int playerColor) throws MoveException {
    if (correctMove(x, y, playerColor)) {
      board[x][y].setState(playerColor);
    } else {
      throw new MoveException();
    }
  }

  /**
   * Removes a stone from the board, if one exists.
   * @param x coordinate
   * @param y coordinate
   */
  void removeStone(int x, int y) { board[x][y].setState(FREE); }

  /**
   * Checks for move validity.
   * @param x coordinate
   * @param y coordinate
   * @param playerColor -1 for black, 1 for white
   * @return true if the player's stone can be put on the intersection.
   */
  public boolean correctMove(int x, int y, int playerColor) {
    
    boolean inbounds = (x < size && x >= 0 && y < size && y >= 0);
    if (!inbounds) return false;

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
   * Sets neighbours of each intersection.
   */
  public void setNeighbours() {
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (i + 1 < size) {
          board[i][j].neighbours.add(board[i + 1][j]);
        }
        if (i - 1 >= 0) {
          board[i][j].neighbours.add(board[i - 1][j]);
        }
        if (j + 1 < size) {
          board[i][j].neighbours.add(board[i][j + 1]);
        }
        if (j - 1 >= 0) {
          board[i][j].neighbours.add(board[i][j - 1]);
        }
      }
    }
  }

  /**
   * Returns the board in a String format. The encoding is specified as following:
   * <pre>
   *white -> W
   *black -> B
   *free  -> E
   * </pre>
   * The board is scanned column by column, and columns are divided by a "|" symbol
   * @return a String object that describes the current board state
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
