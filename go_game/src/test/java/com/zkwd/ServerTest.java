package com.zkwd;

import com.zkwd.server.GoServer;
import java.io.IOException;

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
  }
}
