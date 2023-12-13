package com.zkwd.server.connection;

import java.net.Socket;

public class Lobby {
    private String code;
    private Socket playerSocket;

    public Lobby(String code, Socket socket){
        this.code = code;
        this.playerSocket = socket;
    }

    public String getCode() {
        return code;
    }

    public Socket getSocket() {
        return playerSocket;
    }
}
