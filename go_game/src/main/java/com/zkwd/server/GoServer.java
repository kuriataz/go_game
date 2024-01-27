package com.zkwd.server;

import com.zkwd.server.connection.SocketReceiver;
import com.zkwd.server.connection.Lobby;
import com.zkwd.server.connection.LobbyInterpreter;
import com.zkwd.server.game.GoGame;
import com.zkwd.server.game.players.CPUPlayer;
import com.zkwd.server.game.players.ClientPlayer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class GoServer {
  private ServerSocket serverSocket;

  // will contain a list of codes, under which some player is awaiting an
  // opponent
  private static ArrayList<Lobby> pendingGames = new ArrayList<Lobby>();

  private static Connection connection;

  public GoServer(int port) throws IOException {
    serverSocket = new ServerSocket(port);

    // TODO : move credentials out of code and into resources
    try {
      System.out.println("connecting to mariadb...");
      connection = DriverManager.getConnection(
          "jdbc:mariadb://localhost:3306/gogame", "server", "server_pass"
      );
    } catch (SQLException e) {
      System.out.println(e.getLocalizedMessage());
    }
  }

  public void start() {
    while (true) {
      try {

        Socket playerSocket = serverSocket.accept();
        SocketReceiver cr = new SocketReceiver(playerSocket);
        new Thread(cr).start();
        new Thread(new LobbyInterpreter(cr)).start();
        
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
          host.reset();
          joinee.reset();

          ClientPlayer a = new ClientPlayer(host);
          ClientPlayer b = new ClientPlayer(joinee);

          String hist = new GoGame(a, b, size).startGame();
          logGame(joinee.getUID(), host.getUID(), hist);

          System.out.println("!!! game ended successfully !!!");
          System.out.println("final history: " + hist);

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }.start();
  }

  /**
   * Create a new game against a CPU opponent.
   * @param host host socket
   * @param size board size
   * @param white does host want to play white?
   */
  public static void createBotGame(SocketReceiver host, int size, boolean white) {
    new Thread() {
      @Override
      public void run() {
        try {
          host.reset();

          ClientPlayer a = new ClientPlayer(host);

          String hist;

          if(white) {
            CPUPlayer b = new CPUPlayer(1);
            hist = new GoGame(a, b, size).startGame();
            logGame(-1, host.getUID(), hist);
          } else {
            CPUPlayer b = new CPUPlayer(-1);
            hist = new GoGame(b, a, size).startGame();
            logGame(host.getUID(), -1, hist);
          }

          System.out.println("!!! game ended successfully !!!");
          System.out.println("final history: " + hist);
          System.out.println("user id of player: " + host.getUID());

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }.start();
  }

  /**
   * Logs a game into the database
   * @param whiteID ID of the white player
   * @param blackID ID of the black player
   * @param history string encoding game history
   * @throws SQLException if an SQL error occurs
   */
  private static void logGame(int whiteID, int blackID, String history) throws SQLException {
    PreparedStatement req = connection.prepareStatement("""
      INSERT INTO Games (white, black, moves)
      VALUES (?, ?, ?)
    """);
    req.setInt(1, whiteID);
    req.setInt(2, blackID);
    req.setString(3, history);

    req.executeUpdate();
    req.close();
  }
}
