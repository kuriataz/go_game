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
    try {
      String clientMessage;
      while ((clientMessage = in.readLine()) != null) {
        System.out.println("Received from client: " + clientMessage);

        // TODO : IMPLEMENT A COMMAND SYSTEM

        if (clientMessage.startsWith("joinlobby:")) {

          // find out if the lobby code is taken
          String arg = clientMessage.substring("joinlobby:".length());
          Socket opponent = GoServer.tryJoin(arg);

          // if so, start a game
          if (opponent != null) {
            //
            GoServer.createNewGame(opponent, playerSocket);
            //
          } else {
            // lobby is not taken, so take the lobby
            // add yourself to waiting list
            GoServer.waitForGame(arg, playerSocket);
            out.println("_wait");
          }

          while(true);

          /**
           * have this thread wait on something that gogame has access to?
           * 
           * if we have it wait on the player socket,
           * we can both send notify from the app (cancel queue),
           * or gogame (when the game is finished). it seems like the best option to me, even if its a bit clunky
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
