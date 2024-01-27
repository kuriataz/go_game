package com.zkwd;

import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;

public class CaptureTest {

  /**
   * Test if white stone is captured in this position:
   * <pre>
   *.......BW
   *........B
   *.........
   * </pre>
   */
  public void testCaptureStoneEdge() {
    Board b = new Board(9);

    try {
      b.putStone(7, 0, -1);
      b.putStone(8, 0, 1);
      b.putStone(8, 1, -1);
      b.removeCapturedStones();
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[8][0].getState() == 0);
  }

  /**
   * Test if black stone is captured in this position:
   * <pre>
   *.........
   *.........
   *....W....
   *...WBW...
   *....W....
   *.........
   *.........
   * </pre>
   */
  public void testCaptureStoneCenter() {
    Board b = new Board(9);
    try {
      b.putStone(4, 4, 1);
      b.putStone(5, 3, 1);
      b.putStone(5, 5, 1);
      b.putStone(5, 4, -1);
      b.putStone(6, 4, 1);
      b.removeCapturedStones();
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[5][4].getState() == 0);
  }

  /**
   * Test if the white chain is captured in this position:
   * <pre>
   *WWB......
   *BB.......
   *.........
   * </pre>
   */
  public void testCaptureChainEdge() {
    Board b = new Board(9);

    try {
      b.putStone(0, 0, 1);
      b.putStone(1, 1, -1);
      b.putStone(0, 1, -1);
      b.putStone(1, 0, 1);
      b.putStone(2, 0, -1);
      b.removeCapturedChains();
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[0][0].getState() == 0);
    assert (b.board[1][0].getState() == 0);
  }

  /**
   * Test if the white chain is captured in this position:
   * <pre>
   *.........
   *.........
   *.........
   *..BB.....
   *.BWWB....
   *..BB.....
   * </pre>
   */
  public void testCaptureChainCenter() {
    Board b = new Board(9);
    try {
      b.putStone(2, 5, -1);
      b.putStone(3, 4, -1);
      b.putStone(3, 6, -1);
      b.putStone(4, 4, -1);
      b.putStone(4, 6, -1);
      b.putStone(4, 5, 1);
      b.putStone(3, 5, 1);
      b.putStone(5, 5, -1);
      b.removeCapturedChains();
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[4][5].getState() == 0);
    assert (b.board[3][5].getState() == 0);
  }
  public void testCaptureSquareChain() {
    Board b = new Board(9);

    try {
      b.putStone(3, 6, 1);
      b.putStone(2, 6, 1);
      b.putStone(2, 5, -1);
      b.putStone(3, 4, -1);
      b.putStone(3, 5, -1);
      b.putStone(2, 4, -1);
      b.putStone(1, 5, 1);
      b.putStone(1, 4, 1);
      b.putStone(2, 3, 1);
      b.putStone(3, 3, 1);
      b.putStone(4, 4, 1);
      b.putStone(4, 5, 1);
      b.removeCapturedChains();
    } catch (MoveException e) {
      e.printStackTrace();
    }

    assert (b.board[2][5].getState() == 0);
    assert (b.board[3][5].getState() == 0);
    assert (b.board[3][4].getState() == 0);
    assert (b.board[2][4].getState() == 0);
  }

  public void testCaptureBigChain() {
    Board b = new Board(9);

    try {
      b.putStone(3, 6, 1);
      b.putStone(2, 6, 1);
      b.putStone(2, 5, 1);
      b.putStone(3, 4, 1);
      b.putStone(3, 5, 1);
      b.putStone(2, 4, 1);
      b.putStone(1, 5, 1);
      b.putStone(1, 4, 1);
      b.putStone(2, 3, 1);
      b.putStone(3, 3, 1);
      b.putStone(4, 4, 1);
      b.putStone(4, 5, 1);
      b.putStone(0, 5, -1);
      b.putStone(0, 4, -1);
      b.putStone(1, 3, -1);
      b.putStone(2, 2, -1);
      b.putStone(3, 2, -1);
      b.putStone(4, 3, -1);
      b.putStone(5, 4, -1);
      b.putStone(5, 5, -1);
      b.putStone(4, 6, -1);
      b.putStone(3, 7, -1);
      b.putStone(2, 7, -1);
      b.putStone(1, 6, -1);
      b.removeCapturedChains();
    } catch (MoveException e) {
      e.printStackTrace();
    }

    assert (b.board[2][5].getState() == 0);
    assert (b.board[3][5].getState() == 0);
    assert (b.board[3][6].getState() == 0);
    assert (b.board[2][6].getState() == 0);
    assert (b.board[2][4].getState() == 0);
    assert (b.board[1][4].getState() == 0);
    assert (b.board[4][5].getState() == 0);
  }
  public void testCaptureAlreadyDeadGroup() {
    Board b = new Board(9);
    try {
      b.putStone(2, 5, -1);
      b.putStone(3, 4, -1);
      b.putStone(3, 6, -1);
      b.putStone(4, 3, -1);
      b.putStone(4, 6, -1);
      b.putStone(5, 4, -1);
      b.putStone(5, 5, -1);
      b.putStone(4, 4, 1);
      b.putStone(4, 5, 1);
      b.putStone(3, 5, -1);

      b.removeCapturedChains();
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[4][4].getState() == 0);
    assert (b.board[4][5].getState() == 0);
  }

  public void testCaptureNotSuicide() {
    Board b = new Board(9);
    try {
      b.putStone(0, 0, 1);
      b.putStone(1, 1, -1);
      b.putStone(0, 1, -1);
      b.putStone(1, 0, 1);
      b.putStone(3, 0, 1);
      b.putStone(2, 1, 1);
      b.putStone(2, 0, -1);
      b.removeCapturedStones();
      b.removeCapturedChains();
    } catch (MoveException e) {
      e.printStackTrace();
    }

    assert (b.board[0][0].getState() == 0);
    assert (b.board[1][0].getState() == 0);
  }
}
