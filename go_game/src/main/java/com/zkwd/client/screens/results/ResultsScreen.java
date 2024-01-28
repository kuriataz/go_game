package com.zkwd.client.screens.results;

import com.zkwd.client.model.App;
import com.zkwd.client.model.AppState;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ResultsScreen extends BorderPane {
    
    public ResultsScreen() {
        super();
        Text txt = new Text("this is the results screen. (currently unused)");

        Button back = new Button("back");
        back.setOnAction(e -> {
        App.changeState(AppState.LOBBY);
        });

        HBox topBar = new HBox(5, back);
        BorderPane.setMargin(topBar, new Insets(5));
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.getStyleClass().add("top-bar");
        this.setTop(topBar);

        this.setCenter(txt);
    }
}
