package com.zkwd.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.zkwd.server.connection.Lobby;
import com.zkwd.server.connection.PlayerHandler;

public class GoServer {
  private ServerSocket serverSocket;

  // will contain a list of codes, under which some player is awaiting an opponent
  private static ArrayList<Lobby> pendingGames = new ArrayList<Lobby>();

  public GoServer(int port) throws IOException {
    serverSocket = new ServerSocket(port);
  }

  public void start() {
    while (true) {
      try {
        Socket playerSocket = serverSocket.accept();
        // Handle the client connection, create a new thread for each client
        new Thread(new PlayerHandler(playerSocket)).start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static Socket tryJoin(String code){
    for(Lobby l : pendingGames){
      if(l.getCode().equals(code)){
        // take the lobby off the pending list
        pendingGames.remove(l);
        //
        return l.getSocket();
      }
    }
    //not found
    return null;
  }

  public static void waitForGame(String code, Socket socket){
    pendingGames.add(new Lobby(code, socket));
  }
}
