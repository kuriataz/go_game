package com.zkwd.server.connection;

import com.zkwd.server.Commands.Command;
import com.zkwd.server.GoServer;
import com.zkwd.server.game.GoGame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

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
  private ArrayList<Command> commands;

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
            try {
              //
              GoGame newGame = new GoGame(opponent, playerSocket);
              newGame.run();
              //
            } catch (Exception e){
              /**
               * TODO : in GoGame, exceptions should be thrown that should end the game (one of the players disconnects, something goes very wrong)
               * because here both players (or the remaining player) can be safely disconnected into the lobby screen
               */
            }
          } else {
            // lobby is not taken, so take the lobby
            // add yourself to waiting list
            GoServer.waitForGame(arg, playerSocket);
            out.println("_wait");
          }
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
