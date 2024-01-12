package com.zkwd.server.connection;

import com.zkwd.server.GoServer;
import java.io.IOException;

public class PlayerHandler implements Runnable {

  /**
   * Communication with user.
   */
  private SocketReceiver receiver;

  //private int boardSize;

  public PlayerHandler(SocketReceiver r) throws IOException {
    this.receiver = r;
  }

  @Override
  public void run() {
    int boardSize = 0;
    try {
      String clientMessage;
      while ((clientMessage = receiver.getNextMessage()) != null) {
        System.out.println("Received from client: " + clientMessage);

        // TODO : IMPLEMENT A (proper) COMMAND SYSTEM

        // if (clientMessage.startsWith("boardSize:")) {
        //   String size = clientMessage.substring("boardSize:".length());
        //   try {
        //     boardSize = Integer.parseInt(size);
        //     continue;
        //   } catch (NumberFormatException e) {
        //   }
        // }

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
            // lobby is not taken, so take the lobby
            // add yourself to waiting list
            foundLobby = GoServer.waitForGame(arg, receiver, boardSize);
            receiver.send("_wait");
          }

          // // wait for a message from app telling if it connected to a match or
          // // cancelled
          // String waitResult = receiver.getNextMessage();

          // System.out.println("res: " + waitResult);

          // if (waitResult.equals("unwait")) {
          //   GoServer.unwait(foundLobby);

          //   // this is trash :(
          //   receiver.send("success");

          // } else if (waitResult.equals("connecting")) {

          //   // pause this thread to prevent it from reading the inputstream
          //   // (indefinitely, for now)
          //   while (true)
          //     ;
          // }

          // we no longer care if the app connected or not. we will keep receiving commands, but they will be ignored


        } else {
          // default response
          receiver.send("_unknowncmd");
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
