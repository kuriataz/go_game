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
  Text txt = new Text();

  int boardsize;

  public GameScreen() {
    super();

    this.boardBuilder = new GUIBoardBuilder();
    this.setBottom(txt);

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
        // 2nd message is starting board
        String startBoard = App.await();
        Platform.runLater(() -> {
          System.out.println(startBoard.split("\\|")[0]);
          boardsize = startBoard.split("\\|")[0].length();
          updateBoard(startBoard);
        });

        if (message.equals("game_black")) {
          txt.setText("black pieces");

        } else {
          txt.setText("white pieces");

          boardString = App.await();

          Platform.runLater(() -> {
            updateBoard(boardString);
            // log.push(boardString);
          });
        }

        /**
         * Game loop
         */
        while (true) {
          // wait for your round
          do {
            message = App.await();
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

    System.out.println("works");
    disableInput();

    double mouseX = event.getX();
    double mouseY = event.getY();

    double circleSize = boardBuilder.CircleSize;
    double gridPadding = boardBuilder.GridPadding;

    int clickedX = Math.min((int)((mouseX) / (2.0 * circleSize + 2.0 * gridPadding)), boardsize - 1);
    int clickedY = Math.min((int)((mouseY) / (2.0 * circleSize + 2.0 * gridPadding)), boardsize - 1);

    // Convert the coordinates to a string format and send it
    String clickedPosition =
        clickedX + " " + clickedY + " " + mouseX + " " + mouseY;

    System.out.println("sending move at: " + clickedPosition + " bs: " + boardsize);

    App.send(clickedPosition);
  };

  public Pane launch() { return this; }
}
