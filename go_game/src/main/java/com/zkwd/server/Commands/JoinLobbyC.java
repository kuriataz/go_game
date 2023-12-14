package com.zkwd.server.Commands;

import com.zkwd.server.GoServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class JoinLobbyC extends Command {
  @Override
  public String execute(String clientMessage, Socket playerSocket) {
    if (clientMessage.startsWith("joinlobby:")) {
      // check lobby
      String arg = clientMessage.substring("joinlobby:".length());
      Socket opponent = GoServer.tryJoin(arg);
      if (opponent != null) {
        // create a game here

        // send out messages to both players that they've been connected
        try {
          new PrintWriter(opponent.getOutputStream(), true).println("_connect");
        } catch (IOException e) {
          e.printStackTrace();
        }
        // out.println("_connect");
        return "_connect";
      } else {
        // add yourself to waiting list
        GoServer.waitForGame(arg, playerSocket);
        return "_wait";
      }
    } else {
      return "error";
    }
  }
}
