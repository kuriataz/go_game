package com.zkwd.client.screens.results;

import com.zkwd.client.model.IScreen;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class ResultsScreen extends BorderPane implements IScreen {
    
    public ResultsScreen() {
        super();
        Text txt = new Text("this is the results screen.");

        this.setCenter(txt);
    }

    public Pane launch() {
        return this;
    }
}
