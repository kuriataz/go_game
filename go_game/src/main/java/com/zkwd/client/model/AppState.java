package com.zkwd.client.model;

import com.zkwd.client.screens.ingame.GameScreen;
import com.zkwd.client.screens.lobby.LobbyScreen;
import com.zkwd.client.screens.login.LoginScreen;
import com.zkwd.client.screens.login.RegisterScreen;
import com.zkwd.client.screens.results.ResultsScreen;

import javafx.scene.layout.Pane;

/**
 * Each state corresponds to a separate part of the application the client can interact with and move between.
 */
public enum AppState {
    LOBBY       (LobbyScreen.class),
    INGAME      (GameScreen.class),
    RESULTS     (ResultsScreen.class),
    LOGIN       (LoginScreen.class),
    REGISTER    (RegisterScreen.class);

    private Class<? extends Pane> c;

    private AppState(Class<? extends Pane> c){
        this.c = c;
    }

    public Pane getState(){
        try{
            return this.c.getConstructor().newInstance();
        } catch (Exception e){
            //something went wrong
            e.printStackTrace();
            return null;
        }
    }
}
