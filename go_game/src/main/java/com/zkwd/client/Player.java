package com.zkwd.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player {
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;

  public Player(String serverAddress, int serverPort) throws IOException {
    socket = new Socket(serverAddress, serverPort);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);
  }

  /**
   * Sends a message to the server and awaits for its response.
   * @param message The transmitted message
   * @return The response of the server
   * @throws IOException
   */
  public String transmit(String message) {
    out.println(message);
    try {
      return in.readLine();
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Await for a message from the server.
   * @return The message, once it is delivered.
   * @throws IOException
   */
  public String await() {
    try {
      return in.readLine();
    } catch (IOException e) {
      return null;
    }
  }

  // public String waitForMove() {

  // }

  // public void start() {
  //   try {
  //     BufferedReader userInput =
  //         new BufferedReader(new InputStreamReader(System.in));

  //     while (true) {
  //       String message = userInput.readLine();
  //       out.println(message);

  //       String response = in.readLine();
  //       System.out.println("Server says: " + response);
  //     }
  //   } catch (IOException e) {
  //     e.printStackTrace();
  //   } finally {
  //     try {
  //       socket.close();
  //     } catch (IOException e) {
  //       e.printStackTrace();
  //     }
  //   }
  // }
}
