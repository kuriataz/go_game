package com.zkwd.client.model;

import com.zkwd.client.ingame.GameScreen;
import com.zkwd.client.lobby.LobbyScreen;
import com.zkwd.client.results.ResultsScreen;

public enum State {
    LOBBY (LobbyScreen.class),
    INGAME (GameScreen.class),
    RESULTS (ResultsScreen.class);

    private Class<IScreen> c;

    private State(Class<IScreen> c){
        this.c = c;
    }
}
