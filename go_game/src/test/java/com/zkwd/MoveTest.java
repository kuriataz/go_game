package com.zkwd;

import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;

public class MoveTest {
  public void testSuicide() {
    Board b = new Board(9);
    try {
      b.putStone(2, 5, -1);
      b.putStone(3, 4, -1);
      b.putStone(3, 6, -1);
      b.putStone(4, 5, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }

    assert (b.correctMove(3, 5, 1) == false);
  }

  public void testCornerSuicide() {
    Board b = new Board(9);
    try {
      b.putStone(0, 1, -1);
      b.putStone(1, 0, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }

    assert (b.correctMove(0, 0, 1) == false);
  }
  public void testChainSuicide() {
    Board b = new Board(9);
    try {
      b.putStone(2, 5, -1);
      b.putStone(3, 4, -1);
      b.putStone(3, 6, -1);
      b.putStone(4, 5, 1);
      b.putStone(4, 4, -1);
      b.putStone(4, 6, -1);
      b.putStone(5, 5, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }

    assert (b.correctMove(3, 5, 1) == false);
  }
}
