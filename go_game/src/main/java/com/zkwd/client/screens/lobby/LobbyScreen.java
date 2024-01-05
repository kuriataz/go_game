package com.zkwd.client.screens.lobby;

import com.zkwd.client.model.App;
import com.zkwd.client.model.AppState;
import com.zkwd.client.model.IScreen;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LobbyScreen extends BorderPane implements IScreen {

  Text txt;
  Button cancel;
  HBox hbox;

  Label join;
  TextField tf;
  Button btn;
  Button three;
  Button nine;

  int boardsize = 7;

  public LobbyScreen() {
    super();

    join = new Label("join game:");
    tf = new TextField();
    btn = new Button("start game");
    btn.setOnAction(this::enterLobby);

    txt = new Text("this is the lobby screen.");
    cancel = new Button("x");
    cancel.setOnAction(this::cancelLobby);
    cancel.setVisible(false);

    hbox = new HBox(5);
    hbox.getChildren().addAll(cancel, txt);
    hbox.setAlignment(Pos.CENTER);

    HBox codeInput = new HBox(5);
    codeInput.getChildren().addAll(join, tf, btn);
    codeInput.setAlignment(Pos.CENTER);

    VBox vbox = new VBox(hbox, codeInput);
    vbox.setAlignment(Pos.CENTER);

    three = new Button("3");
    nine = new Button("9");
    HBox boardSize = new HBox(3);
    boardSize.getChildren().addAll(three, nine);

    this.setCenter(vbox);
    this.setBottom(boardSize);
  }

  // Event handler for the button
  private void enterLobby(ActionEvent event) {
    String code = tf.getText();

    // send a command to the server to check if the code is taken
    String result = App.transmit("joinlobby:" + boardsize + ":c" + code);

    if (result.equals("_wait")) {
      // waiting screen
      txt.setText("awaiting an opponent...");
      btn.setDisable(true);
      tf.setDisable(true);
      cancel.setVisible(true);

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
    waitService.reset();
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
          txt.setText("this is the lobby screen.");
        }
      };
    }
  };

  public Pane launch() { return this; }
}
