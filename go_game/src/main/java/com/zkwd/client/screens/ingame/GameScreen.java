package com.zkwd.client.screens.ingame;

import com.zkwd.client.model.App;
import com.zkwd.client.model.AppState;
import com.zkwd.client.util.ConfirmPane;
import com.zkwd.client.util.GUIBoardBuilder;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;

public class GameScreen extends BorderPane {

  private volatile boolean left = false;
  private volatile boolean reqd = false;

  GUIBoardBuilder boardBuilder;

  Group board;
  String boardString;
  Text txt = new Text();

  Button rbtn;
  Button exitbtn;

  int boardsize;

  public GameScreen() {
    super();
    System.out.println("!! creating new GameScreen");

    exitbtn = new Button("exit");
    exitbtn.setOnMouseClicked(abdHandler);

    rbtn = new Button("end game");
    rbtn.setOnMouseClicked(reqHandler);

    txt.setTextAlignment(TextAlignment.CENTER);

    Pane buffer = new Pane();
    Pane buffer2 = new Pane();
    HBox topBar = new HBox(5, exitbtn, buffer, txt, buffer2, rbtn);
    topBar.setAlignment(Pos.CENTER);
    HBox.setHgrow(buffer, Priority.ALWAYS);
    HBox.setHgrow(buffer2, Priority.ALWAYS);
    BorderPane.setMargin(topBar, new Insets(5));
    topBar.setAlignment(Pos.TOP_RIGHT);
    topBar.getStyleClass().add("top-bar");
    this.setTop(topBar);

    this.boardBuilder = new GUIBoardBuilder();

    // begin game
    runGame();
    System.out.println("!! exiting GameScreen");
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

          Window w = rbtn.getScene().getWindow();
          w.setWidth(30 * boardsize + 300);
          w.setHeight(30 * boardsize + 200);
        });

        if (message.equals("game_black")) {
          txt.setText("black pieces");
        } else {
          txt.setText("white pieces");
        }

        boolean turn;

        while(!left) {
          Platform.runLater(() -> {
            disableInput();
          });
          // await turn signal
          message = App.await();

          // check if this player's turn
          turn = message.equals("game_go");

          // loop:
          if (turn) {
            // !! PLAYER TURN
            // listen for reqend/cont
            // if reqend, ask
            System.out.println("\t my turn! awaiting req info...");
            if(App.await().equals("game_reqend")) {
              // display choice pane
              System.out.println("\t end requested! displaying...");
              Platform.runLater(() -> {
                reqEnd();
              });
              reqd = true;
            }

            Platform.runLater(() -> {
              enableInput();
            });
            // additionally, allow move input/reqend
            // if move is reqend, display "awaiting response..."
          }

          System.out.println("\t awaiting end info...");
          // listen for end/cont
          // if received end, go to results
          if(App.await().equals("game_end")) {
            App.changeState(AppState.RESULTS);
            return;
          }

          // if received cont, listen for err or abd

          if(reqd) {
            reqd = false;
            continue;
          }

          // if received abd, ask for bot
          // if yes, send bot
          // if received exit, quit

          System.out.println("\t awaiting abdn/validity info..");
          String req;
          do {
            if (left) {
              return;
            }

            do {
              req = App.await();

            } while (!req.equals("game_abdn") && !req.equals("game_err") && !req.equals("game_vrfd"));
  
            if(req.equals("game_abdn")) {
              Platform.runLater(() -> {
                reqBot();
              });
            } else if(req.equals("game_err")) {
              App.changeState(AppState.LOBBY);
              return;
            } 
          } while(!req.equals("game_vrfd"));

          disableInput();
          
          // if invalid, ->loop
          
          // if valid, disable all input

          // there is a possibility the window is closed now
          // if this happens, abd is sent anyway
          // in which case, the receiver marks it down

          // move validation (reqend is a valid move)
          // after validaing gogame will recheck if an abd happened
          
          // await abd or err
          // if abd, ask for bot
          // if yes, send bot
          // if err, quit
          System.out.println("\t awaiting final abdn info...");
          req = App.await();
          if (req.equals("game_abdn")) {
            reqBot();
          } else if (req.equals("game_err")) {
            App.changeState(AppState.LOBBY);
            return;
          }

          System.out.println("\t awaiting board info...");
          // wait for new board
          String newBoard = App.await();
          Platform.runLater(() -> {
            updateBoard(newBoard);
          });
        }
      }
    }.start();
  }

  /**
   * Ask whether the player wants to end the game.
   */
  private void reqEnd() {
    ConfirmPane c = new ConfirmPane("""
      your opponent has requested to end the match. do you accept?
    """);

    c.yes.setOnMouseClicked(e -> { 
      App.send("yes");
    });
    c.no.setOnMouseClicked(e -> {
      // send any normal code
      App.send("no");
      this.setCenter(board);
    });

    this.setCenter(c);
  }

  /**
   * Asks whether the player wants to continue playing with a bot.
   */
  private void reqBot() {
    ConfirmPane c = new ConfirmPane("""
      your opponent abandoned the match. continue playing against a bot?
    """);

    c.yes.setOnMouseClicked(e -> { 
      App.send("yes");
      this.setCenter(board);
    });
    c.no.setOnMouseClicked(e -> {
      // send any normal code
      App.send("no");
      App.changeState(AppState.LOBBY);
      left = true;
    });

    this.setCenter(c);
  }

  /**
   * Update the board with a new board string.
   * @param boardString string encoding a board.
   */
  private void updateBoard(String boardString) {
    board = boardBuilder.DisplayBoard(boardString);
    this.setCenter(board);
  }

  /**
   * Enable the player to input a move.
   */
  private void enableInput() {
    System.out.println("awaiting user input");
    board.setOnMouseClicked(clickHandler);
    rbtn.setDisable(false);
  }

  /**
   * Remove the player's ability to input a move.
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
        "move:" + clickedX + "," + clickedY;

    System.out.println("sending: " + clickedPosition);

    App.send(clickedPosition);
  };

  /**
   * The move for requesting the game be ended is (-1, x).
   */
  EventHandler<MouseEvent> reqHandler = event -> {
    System.out.println("requesting game end");
    disableInput();

    reqd = true;

    App.send("move:-1,0");
  };

  /**
   * The code for signaling we've abandoned the game is (-2, 0)
   */
  EventHandler<MouseEvent> abdHandler = event -> {
    ConfirmPane c =
        new ConfirmPane("are you sure you want to abandon the game?");
    exitbtn.setDisable(true);

    c.yes.setOnMouseClicked(e -> {
      //
      System.out.println("abandoning game");
      disableInput();
      // send server the exit code
      App.send("exit");
      App.changeState(AppState.LOBBY);
      left = true;
      //
    });
    c.no.setOnMouseClicked(e -> {
      //
      this.setCenter(board);
      exitbtn.setDisable(false);
      //
    });

    this.setCenter(c);
  };
}
