package com.zkwd;

import java.util.Random;

/**
 * Stores game information: the size of the board and placement of pieces on it.
 */
public class Board {

  public static final int BLACK = -1;
  public static final int WHITE = 1;
  public static final int FREE = 0;

  int size;
  int[][] board;

  public Board(int size) {
    this.size = size;
    this.board = new int[size][size]; // by default every cell is 0 = EMPTY
  }

  void putBlack(int x, int y) {
    if (validMove(board[x][y])) {
      board[x][y] = BLACK;
    }
  }

<<<<<<< HEAD
  void putWhite(int x, int y) {
    if (validMove(board[x][y])) {
      board[x][y] = WHITE;
=======
    /**
     * Randomizes the board state. Mostly for debug purposes.
     * @return The board that has been randomized (this)
     */
    public Board randomize() {
        Random r = new Random();
        for(int i = 0; i < size; ++i){
            for(int j = 0; j < size; ++j){
                board[i][j] = r.nextInt() % 3 - 1;
            }
        }
        return this;
    }

    int getSize() {
        return this.size;
    }

    int getValue(int x, int y){
        return board[x][y];
    }

    void putBlack(int x, int y) {
        board[x][y] = BLACK;
>>>>>>> 92b014786e58ac46a4e310e0bb0f4e473484b90d
    }
  }

  void removeStone(int x, int y) {
    if (validMove(board[x][y])) {
      board[x][y] = FREE;
    }
  }

  Boolean validMove(int state) {
    if (state == 0) {
      return true;
    }
    return false;
  }
}
