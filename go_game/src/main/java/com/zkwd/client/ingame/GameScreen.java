package com.zkwd.client.ingame;

import com.zkwd.client.model.IScreen;

import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class GameScreen extends BorderPane implements IScreen {
    
    GameScreen() {
        super();
        Text txt = new Text("this is the ingame screen");

        this.setCenter(txt);
    }
}
