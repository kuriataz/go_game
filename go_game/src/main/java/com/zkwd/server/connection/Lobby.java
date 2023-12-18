package com.zkwd.server.connection;

import java.net.Socket;

public class Lobby {
    private String code;
    private Socket playerSocket;
    private int boardSize;

    public Lobby(String code, Socket socket, int boardSize){
        this.code = code;
        this.playerSocket = socket;
        this.boardSize = boardSize;
    }

    public String getCode() {
        return code;
    }

    public Socket getSocket() {
        return playerSocket;
    }

    public int getBoardSize () {
        return boardSize;
    }
}
