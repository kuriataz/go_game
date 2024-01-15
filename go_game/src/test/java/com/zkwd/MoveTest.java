package com.zkwd;

import com.zkwd.server.game.gamestate.Board;

public class MoveTest {
  public void testSuicide() {
    Board b = new Board(9);
    b = b.setBoard("EEEEEEEEE|EEEEEEEEE|EEWEEEEEE|EWEWEBEEE|EEWEBEBEE|"
                   + "EEEEEBEEE|EEEEEEEEE|EEEEEEEEE|EEEEEEEEE|");

    assert (b.correctMove(3, 2, -1) == false);
    assert (b.correctMove(4, 5, 1) == false);
  }

  public void testSideSuicide() {
    Board b = new Board(9);
    b = b.setBoard("EWEEEEEBE|WEEEEEEEB|EEEEEEEEE|EEEEEEEEE|EEEEEEEEE|"
                   + "EEEEEEEEE|EEEEEEEEE|EEEEEEEEE|EEEEEEEEE|");

    assert (b.correctMove(0, 0, -1) == false);
    assert (b.correctMove(0, 8, 1) == false);
  }
  public void testChainSuicide() {
    Board b = new Board(9);
    b = b.setBoard("EEEEEEEBW|EEEBBBEBE|EEEEEEEEB|EEEEEEEEE|EEEWEEEEE|" +
                   "EEWBWEEEE|EEWEWEEEE|EEEWEEEEE|EEEEEEEEE|");

    // assert (b.correctMove(7, 3, -1) == false);
    // assert (b.correctMove(1, 8, 1) == false);
  }
}
