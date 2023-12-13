package com.zkwd.client.results;

import com.zkwd.client.model.IScreen;

import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class ResultsScreen extends BorderPane implements IScreen {
    
    ResultsScreen() {
        super();
        Text txt = new Text("this is the results screen.");

        this.setCenter(txt);
    }
}
