package com.zkwd.server.game;

import javafx.util.Pair;

public interface Player {
    
    public Pair<Integer, Integer> getMove() throws MoveException, GameException;

    public void sendMessage(String message);
}
