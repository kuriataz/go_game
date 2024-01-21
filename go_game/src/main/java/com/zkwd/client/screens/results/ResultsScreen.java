package com.zkwd.client.screens.results;

import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class ResultsScreen extends BorderPane {
    
    public ResultsScreen() {
        super();
        Text txt = new Text("this is the results screen. (currently unused)");

        this.setCenter(txt);
    }
}
