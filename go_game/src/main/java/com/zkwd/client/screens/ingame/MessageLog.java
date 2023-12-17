package com.zkwd.client.screens.ingame;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
        if(this.getChildren().size() > length){
            this.getChildren().remove(0);
        }

        for(int i = 0; i < this.getChildren().size(); ++i){
            Label l = ((Label) this.getChildren().get(i));
            l.setTextFill(new Color(0, 0, 0, Math.min(1.0, (i + 7.0 - this.getChildren().size()) / 5.0)));
        }
    }

}
