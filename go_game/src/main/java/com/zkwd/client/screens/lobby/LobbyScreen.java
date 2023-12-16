package com.zkwd.client.screens.lobby;

import com.zkwd.client.model.App;
import com.zkwd.client.model.AppState;
import com.zkwd.client.model.IScreen;
import javafx.concurrent.Task;
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
  Label join;
  TextField tf;
  Button btn;

  public LobbyScreen() {
    super();
    this.txt = new Text("this is the lobby screen.");

    this.join = new Label("join game:");
    this.tf = new TextField();
    this.btn = new Button("start game");
    btn.setOnAction(this::handleButtonAction);

    HBox codeInput = new HBox(5);
    codeInput.getChildren().addAll(join, tf, btn);
    codeInput.setAlignment(Pos.CENTER);

    VBox vbox = new VBox(txt, codeInput);
    vbox.setAlignment(Pos.CENTER);

    this.setCenter(vbox);
  }

  // Event handler for the button
  private void handleButtonAction(ActionEvent event) {
    String code = tf.getText();

    // send a command to the server to check if the code is taken
    String result = App.transmit("joinlobby:" + code);

    if (result.equals("_wait")) {
      // waiting screen
      txt.setText("awaiting an opponent...");
      btn.setDisable(true);
      tf.setDisable(true);

      Thread th = new Thread(waiter);
      th.setDaemon(true);
      th.start();

      // put up like a loading wheel or something would be neat
      // also a button to cancel

    } else if (result.equals("_connect")) {
      App.changeState(AppState.INGAME);
    } else {
      // incorrect result
      // communicate that something went wrong
    }
  }

  public Pane launch() { return this; }

  /**
   * Waits for an opponent to join the lobby in the background
   */
  Task<String> waiter = new Task<String>() {
    @Override
    protected String call() {
      return App.await();
    }

    @Override
    protected void succeeded() {
      super.succeeded();

      if(this.getValue().equals("_connect"))
        App.changeState(AppState.INGAME);
    }

    @Override
    protected void cancelled() {
      super.cancelled();
      // request to remove from list
      // TODO : a command that does this
    }
  };
}
