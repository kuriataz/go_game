package com.zkwd;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GoServer {
  private ServerSocket serverSocket;

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
}
