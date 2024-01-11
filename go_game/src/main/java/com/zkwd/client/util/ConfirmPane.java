package com.zkwd.client.util;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Utility Pane that displays a label and two buttons under it, for confirming or declining something.
 */
public class ConfirmPane extends StackPane {

    /**
     * TODO: probably make these private and add functions to apply event handlers?
     */
    public Button yes;
    public Button no;

    public ConfirmPane(String text) {
        super();

        Label txt = new Label(text);

        yes = new Button("yes");
        no = new Button("no");

        HBox btnBox = new HBox(50, yes, no);
        btnBox.setAlignment(Pos.CENTER);
        VBox mainBox = new VBox(20, txt, btnBox);
        mainBox.setAlignment(Pos.CENTER);

        this.getChildren().add(mainBox);
    }

    public ConfirmPane() {
        this("testing!");
    }
}
