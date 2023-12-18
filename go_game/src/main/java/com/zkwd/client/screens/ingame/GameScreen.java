package com.zkwd.client.screens.ingame;

import com.zkwd.client.model.App;
import com.zkwd.client.model.IScreen;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GameScreen extends BorderPane implements IScreen {

  GUIBoardBuilder boardBuilder;

  Group board;
  String boardString;
  Text txt;

  public GameScreen() {
    super();

    this.boardBuilder = new GUIBoardBuilder();
    this.board = boardBuilder.DisplayBoard(boardString);
    this.setBottom(txt);
    this.setCenter(board);

    // begin game
    runGame();
  }

  /**
   * @return True if this player has won the game.
   */
  private boolean runGame() {
    new Thread() {
      public void run() {

        // first message is color
        String message = App.await();
        if (message.equals("game_black")) {
          txt.setText("black pieces");
        } else {
          txt.setText("white pieces");

          // String oppUpdate = App.await(); // later it will be sent to db

          // Platform.runLater(() -> {
          //   // log.push(oppUpdate);
          // });
        }

        /**
         * Game loop
         */
        while (true) {
          // wait for your round
          do {
            message = App.await();
            Platform.runLater(() -> { disableInput(); });
          } while (!message.equals("game_go"));

          String verdict;
          // take inputs until server decides input is correct
          do {
            Platform.runLater(() -> { enableInput(); });

            verdict = App.await();
          } while (!verdict.equals("game_correct"));

          boardString = App.await();

          Platform.runLater(() -> {
            updateBoard(boardString);
            // log.push(boardString);
          });
        }
      }
    }.start();

    return true;
  }

  private void updateBoard(String boardString) {
    board = boardBuilder.DisplayBoard(boardString);
    this.setCenter(board);
  }

  private void enableInput() {
    System.out.println("awaiting user input");
    board.setOnMouseClicked(clickHandler);
  }

  // not sure this is the proper way to do this
  private void disableInput() { board.setOnMouseClicked(null); }

  /**
   * X and Y are in local space, so the handler should be applied to the entire
   * board to calculate properly.
   */
  EventHandler<MouseEvent> clickHandler = event -> {

    double mouseX = event.getX();
    double mouseY = event.getY();

    double circleSize = boardBuilder.CircleSize;
    double gridPadding = boardBuilder.GridPadding;

    int clickedX = (int)((mouseX - gridPadding) / (2 * circleSize + 1));
    int clickedY = (int)((mouseY - gridPadding) / (2 * circleSize + 1));

    // Convert the coordinates to a string format and send it
    String clickedPosition = clickedX + " " + clickedY;

    App.send(clickedPosition);
  };

  public Pane launch() { return this; }
}
