package com.zkwd.server.game.players;

import com.zkwd.server.game.exceptions.GameException;
import com.zkwd.server.game.exceptions.MoveException;

import javafx.util.Pair;

public interface Player {
    
    public Pair<Integer, Integer> getMove() throws MoveException, GameException;

    public void sendMessage(String message);
}
