package com.zkwd;

import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;

public class HistoryTest {
  public void testSimple() {

    Board b = new Board(9);
    try {
      b.putStone(0, 0, -1);
      b.putStone(8, 8, 1);
      b.putStone(8, 0, -1);
      b.putStone(0, 8, 1);
    } catch (MoveException e) {
      e.printStackTrace();
    }

    assert (b.getHistory().equals("BaaWiiBiaWai"));
  }
  public void testCapture() {

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

    assert (b.getHistory().equals("WdgWcgBcfBdeBdfBceWbfWbeWcdWddWeeWef"));
  }
  public void testGetMove() {

    Board b = new Board(9);
    try {
      b.putStone(0, 0, -1);
      b.putStone(8, 8, 1);
      b.putStone(8, 0, -1);
      b.putStone(0, 8, 1);
    } catch (MoveException e) {
      e.printStackTrace();
    }

    assert (b.getHistory().equals("BaaWiiBiaWai"));

    String move = b.getMove(2);
    assert (move.equals("Bia"));
  }
}
