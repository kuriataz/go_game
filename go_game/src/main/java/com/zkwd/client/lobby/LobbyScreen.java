package com.zkwd.client.lobby;

import com.zkwd.client.model.IScreen;

import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class LobbyScreen extends BorderPane implements IScreen {
    
    LobbyScreen(){
        super();
        Text txt = new Text("this is the lobby screen.");

        this.setCenter(txt);
    }
}
