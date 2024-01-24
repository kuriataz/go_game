package com.zkwd.server.game.players;

public interface Player {

    // send a message to the player
    public void sendMessage(String message);

    // get next and last messages from player
    public String getNextMessage();
    public String getLastMessage();

    // ask whether they want to end the game
    // (or to replace other player with bot)
    public boolean requestConfirmation();

    public void clear();
}
