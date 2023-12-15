package com.zkwd.server.connection;

import com.zkwd.server.Commands.Command;
import com.zkwd.server.GoServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class PlayerHandler implements Runnable {

  private Socket playerSocket;
  private BufferedReader in;
  private PrintWriter out;
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
          // check lobby
          String arg = clientMessage.substring("joinlobby:".length());
          Socket opponent = GoServer.tryJoin(arg);
          if (opponent != null) {
            // create a game here

            // send out messages to both players that they've been connected
            new PrintWriter(opponent.getOutputStream(), true)
                .println("_connect");
            out.println("_connect");
          } else {
            // add yourself to waiting list
            GoServer.waitForGame(arg, playerSocket);
            out.println("_wait");
          }
        } else if (clientMessage.startsWith("makemove:")) {
          String arg = clientMessage.substring("makemove:".length());
          // Split the coordinates using the comma as a delimiter
          String[] coordinates = arg.split(",");

          // Convert the coordinates to integers
          if (coordinates.length == 2) {
            try {
              int clickedX = Integer.parseInt(coordinates[0]);
              int clickedY = Integer.parseInt(coordinates[1]);

              // TODO :  CHECK IF IT IS CORRECT, SAVE BOARD CHANGES, GENERATE
              // STRING FOR BUILDER

            } catch (NumberFormatException e) {
              e.printStackTrace();
            }
          }
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
