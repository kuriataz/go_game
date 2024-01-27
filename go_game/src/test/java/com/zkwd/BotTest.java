package com.zkwd;

import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;
import com.zkwd.server.game.players.CPUPlayer;

public class BotTest {
  public void testPriority() {
    Board b = new Board(9);

    try {
      b.putStone(0, 0, -1);
    } catch (MoveException e) {
      e.printStackTrace();
    }
    CPUPlayer bot = new CPUPlayer(b, 1);

    assert (bot.preferredMove.equals("move:1,0") ||
            bot.preferredMove.equals("move:0,1"));
  }
}
