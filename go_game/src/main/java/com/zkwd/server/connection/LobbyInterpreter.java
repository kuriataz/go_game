package com.zkwd.server.connection;

import com.zkwd.server.GoServer;
import java.io.IOException;

public class LobbyInterpreter implements Runnable {

  /**
   * Communication with user.
   */
  private SocketReceiver receiver;

  //private int boardSize;

  public LobbyInterpreter(SocketReceiver r) throws IOException {
    this.receiver = r;
  }

  @Override
  public void run() {
    int boardSize = 0;
    try {
      String clientMessage;
      while (!receiver.isClosed() && !receiver.isClosed()) {
        clientMessage = receiver.getNextMessage();
        System.out.println("lobby thread received: " + clientMessage);

        if (clientMessage.startsWith("joinlobby:")) {

          // find out if the lobby code is taken
          boardSize = Integer.parseInt(clientMessage.split(":")[1]);
          String arg = clientMessage.split(":")[2].substring(1);

          Lobby foundLobby = GoServer.tryJoin(arg, boardSize);

          // if so, start a game
          if (foundLobby != null) {
            //
            GoServer.createNewGame(foundLobby.getReceiver(), receiver,
                                   foundLobby.getBoardSize());
            //
          } else {
            System.out.println("lobby not found: waiting for code " + arg);
            // lobby is not taken, so take the lobby
            // add yourself to waiting list
            foundLobby = GoServer.waitForGame(arg, receiver, boardSize);
            receiver.send("_wait");
          }

          // wait for a message from app telling if it connected to a match or
          // cancelled
          String waitResult = receiver.getNextMessage();

          System.out.println("res: " + waitResult);

          if (waitResult.equals("unwait")) {
            GoServer.unwait(foundLobby);
            receiver.send("success");
            System.out.println("no longer waiting for code " + foundLobby.getCode());

          } else if (waitResult.equals("bot_white")) {
            GoServer.unwait(foundLobby);
            System.out.println("starting a bot match as white");
            GoServer.createBotGame(receiver, boardSize, false);
          } else if (waitResult.equals("bot_black")) {
            GoServer.unwait(foundLobby);
            System.out.println("starting a bot match as black");
            GoServer.createBotGame(receiver, boardSize, true);
          }

          // we no longer care if the app connected or not. we will keep receiving commands, but they will just be ignored

        } else {
          // default response
          // receiver.send("_unknowncmd");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        receiver.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
