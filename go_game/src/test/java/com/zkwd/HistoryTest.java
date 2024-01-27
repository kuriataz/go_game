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
}
