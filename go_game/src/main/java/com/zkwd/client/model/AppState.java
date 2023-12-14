package com.zkwd.client.model;

import com.zkwd.client.screens.ingame.GameScreen;
import com.zkwd.client.screens.lobby.LobbyScreen;
import com.zkwd.client.screens.results.ResultsScreen;

public enum AppState {
    LOBBY (LobbyScreen.class),
    INGAME (GameScreen.class),
    RESULTS (ResultsScreen.class);

    private Class<? extends IScreen> c;

    private AppState(Class<? extends IScreen> c){
        this.c = c;
    }

    public IScreen getState(){
        try{
            return this.c.getConstructor().newInstance();
        } catch (Exception e){
            //something went wrong
            e.printStackTrace();
            return null;
        }
    }
}
