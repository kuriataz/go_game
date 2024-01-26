package com.zkwd.client.screens.login;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zkwd.client.model.App;
import com.zkwd.client.model.AppState;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoginScreen extends BorderPane {

  private TextField f_user = new TextField();
  private TextField f_pass = new TextField();
  
  public LoginScreen() {
    super();

    Label l_user = new Label("username:");
    Label l_pass = new Label("password:");

    f_user = new TextField();
    f_pass = new TextField();

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
    inputGrid.add(f_user, 1, 0);
    inputGrid.add(f_pass, 1, 1);

    Button submit = new Button("submit");
    submit.setOnAction(e -> {
      attemptLogin();
    });

    VBox vbox = new VBox(5, inputGrid, submit);
    vbox.setAlignment(Pos.CENTER);

    this.setCenter(vbox);
  }

  /**
   * Attempt to log into the application
   */
  private void attemptLogin() {
    System.out.println("logging in as " + f_user.getText() + "...");

    try {
      PreparedStatement passcheck = App.getConnection().prepareStatement("""
        SELECT COUNT(id), MIN(id)
        FROM Users
        WHERE username = ?
        AND pass = SHA2(CONCAT(salt, ? ), 256);
      """);

      passcheck.setString(1, f_user.getText());
      passcheck.setString(2, f_pass.getText());

      ResultSet res = passcheck.executeQuery();
      res.next();

      if (res.getInt(1) > 0) {
        // there is a match for user and password
        // set user ID so the application can use it
        // for queries later
        App.setUserId(res.getInt(2));
        App.setUserName(f_user.getText());

        App.changeState(AppState.LOBBY);
      } else {
        // login failed
        System.out.println("login failed :(");
      }

      passcheck.close();
    } catch (SQLException e) {
      System.out.println("oopsie!: " + e.getLocalizedMessage());
    }
  }
}
