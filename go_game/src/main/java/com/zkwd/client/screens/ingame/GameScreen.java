package com.zkwd.client.screens.ingame;

import com.zkwd.client.model.App;
import com.zkwd.client.model.AppState;
import com.zkwd.client.util.ConfirmPane;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class GameScreen extends BorderPane {

  GUIBoardBuilder boardBuilder;

  Group board;
  String boardString;
  Text txt = new Text();

  Button rbtn;
  Button exitbtn;

  int boardsize;

  public GameScreen() {
    super();

    exitbtn = new Button("exit");
    this.setTop(exitbtn);
    exitbtn.setOnMouseClicked(abdHandler);

    this.boardBuilder = new GUIBoardBuilder();

    rbtn = new Button("end game");
    rbtn.setOnMouseClicked(reqHandler);

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

          rbtn.setDisable(true);

          boardString = App.await();
          if(boardString.equals("game_err")) {
            Platform.runLater(() -> { App.changeState(AppState.LOBBY); });
            return;
          }

          Platform.runLater(() -> {
            updateBoard(boardString);
            // log.push(boardString);
          });
        }

        /**
         * Contains the codes that the app responds to.
         * game_go    - it is this user's round
         * game_req   - the other player has requested to end the game
         * game_exit  - exit the game into results screen
         * game_err   - exit the game into lobby screen
         */
        Set<String> validCodes = new HashSet<String>(
            Arrays.asList("game_go", "game_req", "game_exit", "game_err"));
        Set<String> exitCodes =
            new HashSet<String>(Arrays.asList("game_exit", "game_err"));

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
            } while (!verdict.equals("game_correct") &&
                     !exitCodes.contains(verdict));
            if (exitCodes.contains(verdict)) {
              // exit the loop
              message = verdict;
              break;
              //
            } else {
              boardString = App.await();
              if(boardString.equals("game_err")) { break; }

              Platform.runLater(() -> {
                updateBoard(boardString);
                // log.push(boardString);
              });

              boardString = App.await();
              if(boardString.equals("game_err")) { break; }

              Platform.runLater(() -> {
                updateBoard(boardString);
                // log.push(boardString);
              });
            }
          } else if (message.equals("game_req")) {
            // put up confirmation screen
            Platform.runLater(() -> { reqConfirm(); });
            //
          } else {
            // game_exit or game_err
            break;
          }
        }

        if (message.equals("game_exit")) {
          Platform.runLater(() -> { App.changeState(AppState.RESULTS); });
          return;
        } else {
          Platform.runLater(() -> { App.changeState(AppState.LOBBY); });
          return;
        }
      }
    }.start();
  }

  private void reqConfirm() {
    ConfirmPane c = new ConfirmPane(
        "the other player has asked to end the game. do you accept?");

    c.yes.setOnMouseClicked((e) -> { App.send("-1 -1"); });
    c.no.setOnMouseClicked((e) -> {
      // send any normal code
      App.send("0 0");
      this.setCenter(board);
    });

    this.setCenter(c);
  }

  private void updateBoard(String boardString) {
    board = boardBuilder.DisplayBoard(boardString);
    this.setCenter(board);
  }

  /**
   * Enables the player to input a move.
   */
  private void enableInput() {
    System.out.println("awaiting user input");
    board.setOnMouseClicked(clickHandler);
    rbtn.setDisable(false);
  }

  /**
   * Prevents the player from inputting a move.
   */
  private void disableInput() {
    board.setOnMouseClicked(null);
    rbtn.setDisable(true);
  }

  /**
   * X and Y are in local space, so the handler should be applied to the entire
   * board to calculate properly.
   */
  EventHandler<MouseEvent> clickHandler = event -> {

    disableInput();

    double mouseX = event.getX();
    double mouseY = event.getY();

    double circleSize = boardBuilder.CircleSize;
    double gridPadding = boardBuilder.GridPadding;

    int clickedX =
        Math.min((int)((mouseX) / (2.0 * circleSize + 2.0 * gridPadding + 2)),
                 boardsize - 1);
    int clickedY =
        Math.min((int)((mouseY) / (2.0 * circleSize + 2.0 * gridPadding + 2)),
                 boardsize - 1);

    // Convert the coordinates to a string format and send it
    String clickedPosition =
        clickedX + " " + clickedY + " " + mouseX + " " + mouseY;

    System.out.println("sending move at: " + clickedPosition +
                       " bs: " + boardsize);

    App.send(clickedPosition);
  };

  /**
   * The move for requesting the game be ended is (-1, x).
   */
  EventHandler<MouseEvent> reqHandler = event -> {
    System.out.println("requesting game end");
    disableInput();

    App.send("-1 0");
  };

  /**
   * The code for signaling we've abandoned the game is (-2, 0)
   */
  EventHandler<MouseEvent> abdHandler = event -> {
    ConfirmPane c =
        new ConfirmPane("are you sure you want to abandon the game?");
    exitbtn.setDisable(true);

    c.yes.setOnMouseClicked((e_yes) -> {
      //
      System.out.println("abandoning game");
      disableInput();
      // send server the exit code
      App.send("exit");
      //
    });
    c.no.setOnMouseClicked((e_no) -> {
      //
      this.setCenter(board);
      exitbtn.setDisable(false);
      //
    });

    this.setCenter(c);
  };
}
