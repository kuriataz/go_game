package com.zkwd.server.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.zkwd.server.GoServer;

public class PlayerHandler implements Runnable {

  private Socket playerSocket;
  private BufferedReader in;
  private PrintWriter out;

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

        if(clientMessage.startsWith("checklobby:")){
          // check lobby
          String arg = clientMessage.substring("checklobby:".length());
          Socket opponent = GoServer.tryJoin(arg);
          if(opponent != null){
            // create a game here

            // send out messages to both players that they've been connected
            new PrintWriter(opponent.getOutputStream(), true).println("_connect");
            out.println("_connect");
          } else {
            // add yourself to waiting list
            GoServer.waitForGame(arg, playerSocket);
            out.println("_wait");
          }
        } else {
          out.println(clientMessage + " - modified");
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
