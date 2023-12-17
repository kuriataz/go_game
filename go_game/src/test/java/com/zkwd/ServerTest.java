package com.zkwd;

import java.io.IOException;

import com.zkwd.client.ServerMessenger;
import com.zkwd.server.GoServer;

/**
 * Class for testing server connection.
 */
public class ServerTest {
  public static void main(String[] args) {
    Thread serverThread = new Thread(() -> {
      try {
        GoServer server = new GoServer(8888);
        server.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    serverThread.start();

    // Start a couple of clients
    // Thread playerThread1 = new Thread(() -> {
    //   try {
    //     Player player1 = new Player("localhost", 8888);
    //     player1.start();
    //   } catch (IOException e) {
    //     e.printStackTrace();
    //   }
    // });
    // playerThread1.start();

    // Thread playerThread2 = new Thread(() -> {
    //   try {
    //     Player player2 = new Player("localhost", 8888);
    //     player2.start();
    //   } catch (IOException e) {
    //     e.printStackTrace();
    //   }
    // });
    // playerThread2.start();
  }
}
