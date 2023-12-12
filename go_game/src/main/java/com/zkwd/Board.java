package com.zkwd;

public class Board {

    public static final int BLACK = -1;
    public static final int WHITE = 1;
    public static final int FREE = 0;

    int size;
    Intersection[][] board;

    public Board(int size) {
        this.size = size;
        this.board = new Intersection [size][size]; // by default every cell is 0 = EMPTY
    }

    void putBlack(int x, int y) {
        board[x][y] = new Intersection(x, y, BLACK);
    }

    void putWhite(int x, int y) {
        board[x][y] = new Intersection(x, y, WHITE);
    }

    void removeStone(int x, int y) {
        board[x][y] = new Intersection(x, y, FREE);
    }
}
