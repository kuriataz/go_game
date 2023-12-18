package com.zkwd.server;

import com.zkwd.server.connection.Lobby;
import com.zkwd.server.connection.PlayerHandler;
import com.zkwd.server.game.GoGame;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GoServer {
  private ServerSocket serverSocket;

  // will contain a list of codes, under which some player is awaiting an
  // opponent
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

  public static Lobby tryJoin(String code) {
    for (Lobby l : pendingGames) {
      if (l.getCode().equals(code)) {
        // take the lobby off the pending list
        pendingGames.remove(l);
        //
        return l;
      }
    }
    // not found
    return null;
  }

  public static void waitForGame(String code, Socket socket) {
    pendingGames.add(new Lobby(code, socket));
  }

  public static void createNewGame(Socket host, Socket joinee, int size) {
    new Thread() {
      @Override
      public void run() {
        try {
          new GoGame(host, joinee).run();
        } catch (Exception e) {
          /**
           * TODO : in GoGame, exceptions should be thrown that should end the
           * game (one of the players disconnects, something goes very wrong)
           * because here both players (or the remaining player) can be safely
           * disconnected into the lobby screen
           */
        }
      }
    }.start();
  }
}
