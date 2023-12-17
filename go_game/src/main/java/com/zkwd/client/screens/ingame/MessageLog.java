package com.zkwd.client.screens.ingame;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MessageLog extends VBox{

    private final int length = 6;

    MessageLog() {
        super();
        this.setAlignment(Pos.CENTER);
    }

    public void push(String line){
        Label lbl = new Label(line);

        this.getChildren().add(lbl);

        // pop oldest child if necessary
        if(this.getChildren().size() >= length){
            this.getChildren().remove(0);
        }
    }
}
