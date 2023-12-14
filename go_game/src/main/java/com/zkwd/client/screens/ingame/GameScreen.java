package com.zkwd.client.screens.ingame;

import com.zkwd.client.model.IScreen;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class GameScreen extends BorderPane implements IScreen {
    
    public GameScreen() {
        super();
        Text txt = new Text("this is the ingame screen");

        this.setCenter(txt);
    }

    public Pane launch() {
        return this;
    }
}
