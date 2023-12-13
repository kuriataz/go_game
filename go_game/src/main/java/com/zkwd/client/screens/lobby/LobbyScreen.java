package com.zkwd.client.screens.lobby;

import com.zkwd.client.model.App;
import com.zkwd.client.model.IScreen;
import com.zkwd.client.model.State;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LobbyScreen extends BorderPane implements IScreen {
    
    public LobbyScreen(){
        super();
        Text txt = new Text("this is the lobby screen.");

        Label join = new Label("join game:");
        TextField tf = new TextField();
        Button btn = new Button("start game");

        HBox codeInput = new HBox(5);
        codeInput.getChildren().addAll(join, tf, btn);
        codeInput.setAlignment(Pos.CENTER);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event){
                // ask server if code is taken
                App.changeState(State.INGAME);
            }
        });

        VBox vbox = new VBox(txt, codeInput);
        vbox.setAlignment(Pos.CENTER);

        this.setCenter(vbox);
    }

    public Pane launch() {
        return this;
    }
}
