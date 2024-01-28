package com.zkwd.client.screens.lobby;

import com.zkwd.client.model.App;
import com.zkwd.client.model.AppState;
import com.zkwd.client.util.ConfirmPane;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LobbyScreen extends BorderPane {

  Text txt;
  Button cancel;
  HBox hbox;

  Label join;
  TextField tf;
  Button btn;
  Button three;
  Button nine;

  Button thirteen;
  Button nineteen;
  HBox sizeBox;

  HBox botMenu;
  VBox vbox;

  String a = "placeholder!";

  int boardSize = 9;

  public LobbyScreen() {
    super();

    Button exitBtn = new Button("exit");
    Button gamesBtn = new Button("past games");
    
    exitBtn.setOnMouseClicked((event) -> {
      // show modal
      ConfirmPane c = new ConfirmPane("are you sure you want to exit?");
      exitBtn.setDisable(true);
      gamesBtn.setDisable(true);

      c.yes.setOnMouseClicked((e_yes) -> {
        Platform.exit();
        // remove self from waiting queue if necessary
        waitService.cancel();
        System.exit(0);
      });
      c.no.setOnMouseClicked((e_no) -> {
        this.setCenter(vbox);
        exitBtn.setDisable(false);
        gamesBtn.setDisable(false);
      });

      this.setCenter(c);
    });

    gamesBtn.setOnMouseClicked(e -> {
      App.changeState(AppState.GAMELOG);
    });

    HBox topBar = new HBox(5, exitBtn, gamesBtn);
    BorderPane.setMargin(topBar, new Insets(5));
    topBar.setAlignment(Pos.TOP_RIGHT);
    topBar.getStyleClass().add("top-bar");
    this.setTop(topBar);

    join = new Label("join game:");
    tf = new TextField();
    btn = new Button("start game");
    btn.setOnAction(this::enterLobby);

    txt = new Text("logged in as: " + App.getUserName());
    cancel = new Button("x");
    cancel.setOnAction(this::cancelLobby);
    cancel.setVisible(false);

    hbox = new HBox(5);
    hbox.getChildren().addAll(cancel, txt);
    hbox.setAlignment(Pos.CENTER);

    HBox codeInput = new HBox(5);
    codeInput.getChildren().addAll(join, tf, btn);
    codeInput.setAlignment(Pos.CENTER);

    nine = new Button("9");
    nine.setOnAction(this::sizeNine);
    thirteen = new Button("13");
    thirteen.setOnAction(this::sizeThirteen);
    nineteen = new Button("19");
    nineteen.setOnAction(this::sizeNineteen);
    sizeBox = new HBox(5);
    sizeBox.getChildren().addAll(nine, thirteen, nineteen);
    sizeBox.setAlignment(Pos.CENTER);

    nine.getStyleClass().add("active");

    Label bot = new Label("play against CPU?: ");
    Button bot1 = new Button("as white");
    Button bot2 = new Button("as black");
    bot1.setOnAction(this::enterBotAsWhite);
    bot2.setOnAction(this::enterBotAsBlack);

    botMenu = new HBox(10, bot, bot2, bot1);
    botMenu.setAlignment(Pos.CENTER);
    botMenu.setVisible(false);

    vbox = new VBox(5, hbox, codeInput, sizeBox, botMenu);
    vbox.setAlignment(Pos.CENTER);

    this.setCenter(vbox);
  }

  // Event handler for the button
  private void enterLobby(ActionEvent event) {
    String code = tf.getText();

    // send a command to the server to check if the code is taken
    String result = App.transmit("joinlobby:" + boardSize + ":c" + code);

    if (result.equals("_wait")) {
      // waiting screen
      txt.setText("awaiting an opponent...");
      btn.setDisable(true);
      tf.setDisable(true);
      cancel.setVisible(true);
      botMenu.setVisible(true);

      System.out.println("service");
      waitService.restart();

      // put up like a loading wheel or something would be neat

    } else if (result.equals("_connect")) {
      App.send("connecting");
      App.changeState(AppState.INGAME);
    } else {
      // incorrect result
      // communicate that something went wrong
    }
  }

  private void cancelLobby(ActionEvent event) {
    // send message to waiter
    waitService.cancel();
  }
  private void enterBotAsWhite(ActionEvent event) {
    App.send("bot_white");
  }
  private void enterBotAsBlack(ActionEvent event) {
    App.send("bot_black");
  }

  private void sizeNine(ActionEvent event) { 
    boardSize = 9;
    nine.getStyleClass().add("active");
    thirteen.getStyleClass().remove("active");
    nineteen.getStyleClass().remove("active");
  }
  private void sizeThirteen(ActionEvent event) { 
    boardSize = 13;
    nine.getStyleClass().remove("active");
    thirteen.getStyleClass().add("active");
    nineteen.getStyleClass().remove("active");
  }
  private void sizeNineteen(ActionEvent event) { 
    boardSize = 19;
    nine.getStyleClass().remove("active");
    thirteen.getStyleClass().remove("active");
    nineteen.getStyleClass().add("active");
  }

  /**
   * Waits for an opponent to join the lobby in the background
   */
  Service<String> waitService = new Service<String>() {
    @Override
    public Task<String> createTask() {
      return new Task<String>() {
        @Override
        protected String call() {
          return App.await();
        }

        @Override
        protected void succeeded() {
          super.succeeded();

          if (this.getValue().equals("_connect"))
            App.send("connecting");
          App.changeState(AppState.INGAME);
        }

        @Override
        protected void cancelled() {
          super.cancelled();

          // request to remove lobby from list
          App.send("unwait");

          btn.setDisable(false);
          tf.setDisable(false);
          cancel.setVisible(false);
          botMenu.setVisible(false);
          txt.setText("this is the lobby screen.");
        }
      };
    }
  };
}
