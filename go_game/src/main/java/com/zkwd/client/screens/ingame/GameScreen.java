package com.zkwd.client.screens.ingame;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.zkwd.client.model.App;
import com.zkwd.client.model.AppState;
import com.zkwd.client.model.IScreen;
import com.zkwd.client.util.ConfirmPane;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
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

  Button rbtn;

  int boardsize;

  public GameScreen() {
    super();

    this.boardBuilder = new GUIBoardBuilder();

    rbtn = new Button("end game");
    rbtn.setOnMouseClicked(reqHandler);
    rbtn.setDisable(true);

    HBox hbox = new HBox(5, txt, rbtn);
    this.setBottom(hbox);

    // begin game
    runGame();
  }

  /**
   * Runs the game loop in a new thread.
   */
  private void runGame() {
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
         * Contains the codes that the app responds to.
         * game_go    - it is this user's round
         * game_req   - the other player has requested to end the game
         * game_exit  - exit the game
         */
        Set<String> validCodes = new HashSet<String>(Arrays.asList("game_go", "game_req", "game_err", "game_exit"));

        /**
         * Game loop
         */
        while (true) {
          // wait for your round
          do {
            System.out.println("awaiting command");
            message = App.await();
          } while (!validCodes.contains(message));

          if (message.equals("game_go")) {
            String verdict;
            // take inputs until server decides input is correct
            do {
              Platform.runLater(() -> { enableInput(); });

              verdict = App.await();
            } while (!verdict.equals("game_correct") && !verdict.equals("game_exit"));
            if(verdict.equals("game_exit")) break;

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
          } else if (message.equals("game_req")) {
            // put up confirmation screen
            Platform.runLater(() -> {
              reqConfirm();
            });
            //
          } else if (message.equals("game_exit")) {
            break;
          } else if (message.equals("game_err")) {
            App.changeState(AppState.LOBBY);
          }
        }

        Platform.runLater(() -> {
          App.changeState(AppState.RESULTS);
        });
      }
    }.start();
  }

  private void reqConfirm() {
    ConfirmPane c = new ConfirmPane("the other player has asked to end the game. do you accept?");

    c.yes.setOnMouseClicked((e) -> {
      App.send("-1 -1");
    });
    c.no.setOnMouseClicked((e) -> {
      App.send("00 00");
      this.setCenter(board);
    });

    this.setCenter(c);
  }

  private void updateBoard(String boardString) {
    board = boardBuilder.DisplayBoard(boardString);
    this.setCenter(board);
  }

  private void enableInput() {
    System.out.println("awaiting user input");
    board.setOnMouseClicked(clickHandler);
    rbtn.setDisable(false);
  }

  // not sure this is the proper way to do this
  private void disableInput() { 
    board.setOnMouseClicked(null);
    rbtn.setDisable(true);
  }

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

  /**
   * The move for requesting the game be ended is (-1, -1).
   */
  EventHandler<MouseEvent> reqHandler = event -> {
    System.out.println("requesting game end");
    disableInput();

    App.send("-1 -1");
  };

  public Pane launch() { return this; }
}
