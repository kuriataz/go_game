package com.zkwd.server.connection;

// import com.zkwd.server.Commands.Command;
import com.zkwd.server.GoServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
// import java.util.ArrayList;

public class PlayerHandler implements Runnable {

  /**
   * Communication
   */
  private Socket playerSocket;
  private BufferedReader in;
  private PrintWriter out;

  private int boardSize;

  /**
   * this is a command log i think? correct me
   */
  // private ArrayList<Command> commands;

  public PlayerHandler(Socket socket) throws IOException {
    this.playerSocket = socket;
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);
  }

  @Override
  public void run() {
    int boardSize = 0;
    try {
      String clientMessage;
      while ((clientMessage = in.readLine()) != null) {
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
            GoServer.createNewGame(foundLobby.getSocket(), playerSocket,
                                   foundLobby.getBoardSize());
            //
          } else {
            // lobby is not taken, so take the lobby
            // add yourself to waiting list
            foundLobby = GoServer.waitForGame(arg, playerSocket, boardSize);
            out.println("_wait");
          }

          // wait for a message from app telling if it connected to a match or
          // cancelled
          String waitResult = in.readLine();

          System.out.println("res: " + waitResult);

          if (waitResult.equals("unwait")) {
            GoServer.unwait(foundLobby);

            // this is trash :(
            out.println("success");

          } else if (waitResult.equals("connecting")) {

            // pause this thread to prevent it from reading the inputstream
            // (indefinitely, for now)
            while (true)
              ;
          }

          /**
           * TODO : implement - wait until game concluded or one of the players
           * has exited.
           */

        } else {
          // default response
          out.println("_unknowncmd");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        playerSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
