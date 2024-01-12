package com.zkwd.server.connection;

public class Lobby {
    private String code;
    private SocketReceiver sr;
    private int boardSize;

    public Lobby(String code, SocketReceiver sr, int boardSize){
        this.code = code;
        this.sr = sr;
        this.boardSize = boardSize;
    }

    public String getCode() {
        return code;
    }

    public SocketReceiver getReceiver() {
        return sr;
    }

    public int getBoardSize () {
        return boardSize;
    }
}
