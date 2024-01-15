package com.zkwd;

import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;

public class CaptureTest {
  public void testCaptureStone() {
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
  public void testCaptureChain() {
    Board b = new Board(9);

    // try {
    //   b.putStone(0, 0, 1);
    //   b.putStone(1, 1, -1);
    //   b.putStone(0, 1, -1);
    //   b.putStone(0, 1, 1);
    //   b.putStone(0, 2, -1);
    //   b.removeCapturedChains();
    // } catch (MoveException e) {
    //   e.printStackTrace();
    // }

    // assert (b.board[0][0].getState() == 0);
    // assert (b.board[0][1].getState() == 0);
    try {
      b.putStone(2, 5, -1);
      b.putStone(3, 4, -1);
      b.putStone(3, 6, -1);
      b.putStone(4, 4, -1);
      b.putStone(4, 6, -1);
      b.putStone(4, 5, 1);
      b.putStone(3, 5, 1);
      b.putStone(4, 6, -1);
      //   b.removeCapturedChains();
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[4][5].getState() == 0);
    assert (b.board[3][5].getState() == 0);
  }
}
