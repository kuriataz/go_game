package com.zkwd.client.screens.ingame;

import com.zkwd.client.model.App;
import com.zkwd.client.model.IScreen;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class GameScreen extends BorderPane implements IScreen {

  GUIBoardBuilder boardBuilder;
  String boardState;

  public GameScreen() {
    super();
    // Text txt = new Text("this is the ingame screen");

    // this.setCenter(txt);
    this.boardBuilder = new GUIBoardBuilder();
    this.boardState = "WWB|BBW|WBB"; // for test

    EventHandler<MouseEvent> clickHandler = event -> {

      double mouseX = event.getX();
      double mouseY = event.getY();

      double circleSize = boardBuilder.CircleSize;
      double gridPadding = boardBuilder.GridPadding;

      int clickedX = (int)((mouseX - gridPadding) / (2 * circleSize + 1));
      int clickedY = (int)((mouseY - gridPadding) / (2 * circleSize + 1));

      // Convert the coordinates to a string format and transmit it
      String clickedPosition = clickedX + " " + clickedY;

      String result = App.transmit("makemove:" + clickedPosition);
      this.boardState = result;
    };

    this.setCenter(boardBuilder.DisplayBoard(boardState, clickHandler));
  }

  public Pane launch() { return this; }
}
