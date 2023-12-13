package com.zkwd.client.screens.lobby;

import com.zkwd.client.model.App;
import com.zkwd.client.model.IScreen;
import com.zkwd.client.model.State;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

        // TODO : maybe move the event handler outside the constructor
        // will have to make the above definitions class-wide, though.
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event){
                String code = tf.getText();

                // send a command to the server to check if the code is taken
                String result = App.transmit("checklobby:" + code);

                while(result.equals("_wait")){
                    // waiting screen
                    txt.setText("awaiting an opponent...");
                    btn.setDisable(true);
                    
                    result = App.await();
                }
                
                if(result.equals("_connect")) {

                    App.changeState(State.INGAME);

                } else {
                    // incorrect result
                    // communicate that something went wrong
                }
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
