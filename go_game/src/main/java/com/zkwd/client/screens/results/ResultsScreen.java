package com.zkwd.client.screens.results;

import com.zkwd.client.model.App;
import com.zkwd.client.model.AppState;
import com.zkwd.client.util.GUIBoardBuilder;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ResultsScreen extends BorderPane {

    private final Label txt;
    
    public ResultsScreen() {
        super();
        txt = new Label("Loading results...");

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

        String bstate = App.await();

        GUIBoardBuilder builder = new GUIBoardBuilder();
        VBox results = new VBox(
            new Label("final board state:"),
            builder.DisplayBoard(bstate), 
            new Label("feel free to calculate the final score now :)")
        );
        results.setAlignment(Pos.CENTER);
        this.setCenter(results);
    }
}
