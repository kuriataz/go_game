package com.zkwd.client.screens.login;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.zkwd.client.model.App;
import com.zkwd.client.model.AppState;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The scene the user sees, when they are trying to register a new account.
 */
public class RegisterScreen extends BorderPane {

    private TextField f_user;
    private TextField f_pass;
    private TextField f_p2ss;
    private Button submit;
    
    public RegisterScreen() {
        super();

        Button log = new Button("login");
        log.setOnAction(e -> {
          App.changeState(AppState.LOGIN);
        });

        this.setTop(log);

        Label l_user = new Label("username:");
        Label l_pass = new Label("password:");
        Label l_p2ss = new Label("confirm:");

        f_user = new TextField();
        f_pass = new TextField();
        f_p2ss = new TextField();

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(12);
        inputGrid.setVgap(10);
        inputGrid.setAlignment(Pos.CENTER);

        // align columns to left and right, respectively
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.RIGHT);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHalignment(HPos.LEFT);
        inputGrid.getColumnConstraints().addAll(col1, col2);

        // add all the elements to grid
        inputGrid.add(l_user, 0, 0);
        inputGrid.add(l_pass, 0, 1);
        inputGrid.add(l_p2ss, 0, 2);
        inputGrid.add(f_user, 1, 0);
        inputGrid.add(f_pass, 1, 1);
        inputGrid.add(f_p2ss, 1, 2);

        submit = new Button("submit");

        VBox box = new VBox(5, inputGrid, submit);
        box.setAlignment(Pos.CENTER);

        this.setCenter(box);

        /**
         * Submit and attempt to register a new account.
         */
        submit.setOnAction((event) -> {
            attemptRegister();
        });

        /**
         * Submit by pressing ENTER if any of the text fields are focused.
         */
        f_user.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                attemptRegister();
            }
        });
        f_pass.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                attemptRegister();
            }
        });
        f_p2ss.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                attemptRegister();
            }
        });

        // put the focus on the username input field
        Platform.runLater(() -> {
            f_user.requestFocus();
        });
    }

    private void attemptRegister() {
        System.out.println("registering as " + f_user.getText() + "...");

        if (f_pass.getText().equals(f_p2ss.getText())) {
            try {
                CallableStatement attRegister = App.getConnection().prepareCall("""
                    CALL add_user(?, ?)
                """);

                attRegister.setString(1, f_user.getText().trim());
                attRegister.setString(2, f_pass.getText().trim());

                attRegister.execute();

                System.out.println("Success!");

                App.changeState(AppState.LOGIN);
                attRegister.close();
            } catch (SQLException e) {
                System.out.println("oops!: " + e.getLocalizedMessage());
            }
        } else {
            // passwords don't match
            System.out.println("passwords dont match!");
        }
    }
}
