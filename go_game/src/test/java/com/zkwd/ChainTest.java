package com.zkwd;

import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;
import java.util.ArrayList;

public class ChainTest {

  public void testNewChain() {
    Board b = new Board(9);
    try {
      b.putStone(2, 5, -1);
      b.putStone(2, 4, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[2][5].chainId == 1);
    assert (b.board[2][4].chainId == 1);
  }

  public void testAddToChain() {
    Board b = new Board(9);
    try {
      b.putStone(2, 5, -1);
      b.putStone(2, 4, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[2][5].chainId == 1);
    assert (b.board[2][4].chainId == 1);
    try {
      b.putStone(1, 5, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[1][5].chainId == 1);
  }

  public void testGainToChain() {
    Board b = new Board(9);
    try {
      b.putStone(2, 5, -1);
      b.putStone(3, 6, -1);
      b.putStone(1, 6, -1);
      b.putStone(2, 7, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[2][5].chainId == 0);
    assert (b.board[3][6].chainId == 0);
    assert (b.board[1][6].chainId == 0);
    assert (b.board[2][7].chainId == 0);
    try {
      b.putStone(2, 6, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[2][5].chainId == 1);
    assert (b.board[3][6].chainId == 1);
    assert (b.board[1][6].chainId == 1);
    assert (b.board[2][7].chainId == 1);
    assert (b.board[2][6].chainId == 1);
  }

  public void testChangeChain() {
    Board b = new Board(9);
    try {
      b.putStone(2, 5, -1);
      b.putStone(2, 4, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[2][5].chainId == 1);
    assert (b.board[2][4].chainId == 1);
    try {
      b.putStone(0, 5, -1);
      b.putStone(0, 4, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[0][5].chainId == 2);
    assert (b.board[0][4].chainId == 2);

    try {
      b.putStone(1, 5, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[2][5].chainId == 1);
    assert (b.board[2][4].chainId == 1);
    assert (b.board[0][5].chainId == 1);
    assert (b.board[0][4].chainId == 1);
    assert (b.board[1][5].chainId == 1);
  }

  public void testFindChain() {
    Board b = new Board(9);
    try {
      b.putStone(2, 5, -1);
      b.putStone(2, 4, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[2][5].chainId == 1);
    assert (b.board[2][4].chainId == 1);

    try {
      b.putStone(0, 5, -1);
      b.putStone(0, 4, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[0][5].chainId == 2);
    assert (b.board[0][4].chainId == 2);

    b.board[1][5].setState(-1);
    ArrayList<Integer> ids = b.board[1][5].findChain();

    assert (ids.isEmpty() == false);
    assert (ids.get(0) == 1);
    assert (ids.get(1) == 2);
  }
}
