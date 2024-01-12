package com.zkwd.server;

import com.zkwd.server.connection.SocketReceiver;
import com.zkwd.server.connection.Lobby;
import com.zkwd.server.connection.PlayerHandler;
import com.zkwd.server.game.GoGame;
import com.zkwd.server.game.players.ClientPlayer;
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
        SocketReceiver cr = new SocketReceiver(playerSocket);
        // Handle the client connection, create a new thread for each client
        new Thread(new PlayerHandler(cr)).start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * C.
   * @param code
   * @param boardSize
   * @return
   */
  public static Lobby tryJoin(String code, int boardSize) {
    for (Lobby l : pendingGames) {
      if (l.getCode().equals(code) && l.getBoardSize() == boardSize) {
        // take the lobby off the pending list
        pendingGames.remove(l);
        //
        return l;
      }
    }
    // not found
    return null;
  }

  /**
   * C.
   * @param code
   * @param socket
   * @param size
   * @return
   */
  public static Lobby waitForGame(String code, SocketReceiver sr, int size) {
    Lobby l = new Lobby(code, sr, size);
    pendingGames.add(l);
    return l;
  }

  /**
   * 
   * @param l
   */
  public static void unwait(Lobby l) { pendingGames.remove(l); }

  /**
   * 
   * @param host
   * @param joinee
   * @param size
   */
  public static void createNewGame(SocketReceiver host, SocketReceiver joinee, int size) {
    new Thread() {
      @Override
      public void run() {
        try {
          ClientPlayer a = new ClientPlayer(host);
          ClientPlayer b = new ClientPlayer(joinee);

          new GoGame(a, b, size).run();
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
