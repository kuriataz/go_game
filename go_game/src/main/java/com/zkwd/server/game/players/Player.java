package com.zkwd.server.game.players;

import com.zkwd.server.game.exceptions.GameException;
import com.zkwd.server.game.exceptions.MoveException;

import javafx.util.Pair;

public interface Player {

    // get a Go move from the player
    public Pair<Integer, Integer> getMove() throws MoveException, GameException;

    // send a message to the player
    public void sendMessage(String message);
}
