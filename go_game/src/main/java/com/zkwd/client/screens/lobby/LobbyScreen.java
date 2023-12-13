package com.zkwd.client.screens.lobby;

import com.zkwd.client.model.App;
import com.zkwd.client.model.IScreen;
import com.zkwd.client.model.State;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LobbyScreen extends BorderPane implements IScreen {
    
    public LobbyScreen(){
        super();
        Text txt = new Text("this is the lobby screen.");
        Button btn = new Button("start game");

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event){
                App.changeState(State.INGAME);
            }
        });

        VBox vbox = new VBox(txt, btn);
        vbox.setAlignment(Pos.CENTER);

        this.setCenter(vbox);
    }

    public Pane launch() {
        return this;
    }
}
