package com.zkwd;
import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;

public class PutTest {
  public void testBlack() {
    Board b = new Board(9);

    // corner
    try {
      b.putStone(0, 0, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    assert (b.board[0][0].getState() == -1);

    // line
    try {
      b.putStone(0, 1, -1);
    } catch (MoveException e) {

      e.printStackTrace();
    }
    assert (b.board[0][1].getState() == -1);

    // middle
    try {
      b.putStone(4, 4, -1);
    } catch (MoveException e) {

      e.printStackTrace();
    }
    assert (b.board[4][4].getState() == -1);
  }
  public void testWhite() {
    Board b = new Board(9);

    // corner
    try {
      b.putStone(0, 0, 1);
    } catch (MoveException e) {

      e.printStackTrace();
    }
    assert (b.board[0][0].getState() == 1);

    // line
    try {
      b.putStone(0, 1, 1);
    } catch (MoveException e) {

      e.printStackTrace();
    }
    assert (b.board[0][1].getState() == 1);

    // middle
    try {
      b.putStone(4, 4, 1);
    } catch (MoveException e) {

      e.printStackTrace();
    }
    assert (b.board[4][4].getState() == 1);
  }
}
