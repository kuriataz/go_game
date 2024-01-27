package com.zkwd;

import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;

public class KoTest {

  public void testKo() {
    Board b = new Board(9);
    try {
      b.putStone(3, 1, -1);
      b.putStone(3, 3, -1);
      b.putStone(2, 2, -1);
      b.putStone(3, 2, 1);
      b.putStone(4, 1, 1);
      b.putStone(4, 3, 1);
      b.putStone(5, 2, 1);
      b.putStone(4, 2, -1);

      b.removeCapturedStones();
    } catch (MoveException e) {
      e.printStackTrace();
    }

    assert (b.correctMove(3, 2, 1) == false);
  }

  public void testHalfKo() {
    Board b = new Board(9);
    try {
      b.putStone(5, 2, -1);
      b.putStone(4, 3, -1);
      b.putStone(6, 3, -1);
      b.putStone(5, 3, 1);
      b.putStone(4, 4, 1);
      b.putStone(6, 4, 1);
      b.putStone(5, 5, 1);
    } catch (MoveException e) {
      e.printStackTrace();
    }

    assert (b.correctMove(5, 4, -1) == true);
  }

  public void testKoAfterBreak() {
    Board b = new Board(9);
    try {
      b.putStone(5, 2, -1);
      b.putStone(4, 3, -1);
      b.putStone(6, 3, -1);
      b.putStone(5, 3, 1);
      b.putStone(4, 4, 1);
      b.putStone(6, 4, 1);
      b.putStone(5, 5, 1);
      b.putStone(5, 4, -1);
      b.removeCapturedStones();
      assert (b.correctMove(5, 3, 1) == false);
      b.putStone(0, 0, 1);
      b.putStone(0, 1, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }

    assert (b.correctMove(5, 3, 1) == true);
  }
}
