package com.zkwd.server.Commands;

import java.net.Socket;

public abstract class Command {
  public abstract String execute(String message, Socket socket);
}
