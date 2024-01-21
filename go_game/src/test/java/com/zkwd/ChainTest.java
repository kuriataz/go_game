package com.zkwd;

import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;
import java.util.ArrayList;

public class ChainTest {

  /**
   * Check if after placing down two adjacent stones,
   * they are formed into a chain.
   */
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

  /**
   * Check that after a chain is formed and another stone is placed
   * adjacent to the chain, that stone becomes part of the chain.
   */
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

  /**
   * Check if after placing a stone between multiple stones and connecting them,
   * they start forming a chain.
   * <pre>
   *..B..    ..B..
   *.B.B. -> .BBB.
   *..B..    ..B..
   * </pre>
   */
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

  /**
   * Check that when two chains are connected, they merge into a single chain.
   */
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

  /**
   * Test the chain detection method findChain.
   */
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
