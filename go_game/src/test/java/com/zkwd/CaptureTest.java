package com.zkwd;

import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;

// TODO: find out why it doesn't work

public class CaptureTest {
  public void testCaptureStone() {
    Board b = new Board(9);
    b = b.setBoard("BEEEEEEEE|BEEEEEEEE|EEEEEEEEE|EEEEEEEEE|EEEEWEEEE|"
                   + "EEEWBWEEE|EEEEEEEEE|BEEEEEEEE|WEEEEEEEE|");

    try {
      b.putStone(8, 1, -1);
      b.removeCapturedStones();
    } catch (MoveException e) {
      e.printStackTrace();
    }
    String newString = b.prepareBoardString();
    System.out.println(newString);

    // assert (b.board[8][0].getState() == 0);
    try {
      b.putStone(6, 4, 1);
      b.removeCapturedStones();
    } catch (MoveException e) {
      e.printStackTrace();
    }
    // assert (b.board[5][4].getState() == 0);
  }
  public void testCaptureChain() {
    Board b = new Board(9);
    b = b.setBoard("BWEEEEEBE|BWEEEEEEB|EWEEEBEEE|EEEEBWBEE|EEEEBWEEE|"
                   + "EEEBEBEEE|WEEEBEEEE|WEEEEEEEE|WEEEEEEEE|");

    try {
      b.putStone(0, 2, 1);
      b.removeCapturedChains();
    } catch (MoveException e) {
      e.printStackTrace();
    }

    // assert (b.board[0][0].getState() == 0);
    // assert (b.board[0][1].getState() == 0);
    try {
      b.putStone(4, 6, -1);
      b.removeCapturedChains();
    } catch (MoveException e) {
      e.printStackTrace();
    }
    // assert (b.board[4][5].getState() == 0);
    // assert (b.board[3][5].getState() == 0);
  }
}
