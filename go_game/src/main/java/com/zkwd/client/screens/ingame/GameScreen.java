package com.zkwd.client.screens.ingame;

import com.zkwd.client.model.App;
import com.zkwd.client.model.IScreen;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class GameScreen extends BorderPane implements IScreen {

  GUIBoardBuilder boardBuilder;

  // autoupdate GUI with changing boardState
  private StringProperty boardState = new SimpleStringProperty("");
  private IntegerProperty round = new SimpleIntegerProperty(0);

  Group board;
  Text roundCount = new Text("");

  public GameScreen() {
    super();

    this.setTop(roundCount);

    boardState.addListener((ChangeListener<String>)
      (obs, newVal, oldVal) -> {
        board = boardBuilder.DisplayBoard(newVal);
        this.setCenter(board);
      }
    );

    round.addListener((ChangeListener<Number>)
      (obs, newVal, oldVal) -> {
        roundCount.setText("round " + newVal);
      }
    );

    this.boardBuilder = new GUIBoardBuilder();
    this.boardState.set("WEE|BEW|EEE"); // for test

    // begin game
    // runGame();
  }

  /**
   * I think im putting the game loop in here so its clearly distinguishable from the setup.
   * @return True if this player has won the game.
   */
  private boolean runGame() {

    // loop needs to run in a background thread so as to not freeze the application.
    new Thread() {
      /**
       * Game loop proper: make a move, and then wait another move.
       * TODO : think about when the loop should end
       */
      public void run() {
        String message = App.await();
        String[] split;

        while(true){
          // wait for your round
          while(!message.equals("game_go") && !message.equals("game_goagain")){
            message = App.await();
          }

          // if this is the first attempt at making a move, a signal will be sent to alter the board
          if(message.equals("game_go")){

            // next signal will be game_[round]_[boardState]
            message = App.await();
            split = message.split("_");

            try {
              round.set(Integer.parseInt(split[1]));
            } catch (NumberFormatException e) {} // doesnt really happen (probably still add this later though!)
            boardState.set(split[2]);
          }

          enableInput();

          /**
           * App proceeds to the next loop iteration and does nothing until it receives a signal to move again ("game_go"
           * or "game_goagain")
           * In that time, a message is only sent to the server if the player clicks on the board.
           * When the player clicks on the board, the app requests to make the move and waits for the server to
           * tell it whether the move is valid or not.
           * If the move is valid, the server moves on to ask the other player.
           * Otherwise, the server gives a "game_goagain" signal to the app, which causes it to move again,
           * but without updating the board this time.
           * 
           * Also, all messages received while waiting for "game_go" or "go_again" are ignored.
           */
        }
      }
    }.run();

    return true;
  }

  private void enableInput() {
    board.setOnMouseClicked(clickHandler);
  }

  // not sure this is the proper way to do this
  private void disableInput() {
    board.setOnMouseClicked(null);
  }

  /**
   * X and Y are in local space, so the handler should be applied to the entire board to calculate properly.
   */
  EventHandler<MouseEvent> clickHandler = event -> {
    double mouseX = event.getX();
    double mouseY = event.getY();

    double circleSize = boardBuilder.CircleSize;
    double gridPadding = boardBuilder.GridPadding;

    int clickedX = (int)((mouseX - gridPadding) / (2 * circleSize + 1));
    int clickedY = (int)((mouseY - gridPadding) / (2 * circleSize + 1));

    // Convert the coordinates to a string format and transmit it
    String clickedPosition = clickedX + " " + clickedY;

    // ask if move is correct
    String result = App.transmit("move:" + clickedPosition);

    if(!result.equals("game_incorrect")){
      boardState.set(result);
    } else {
      // TODO : make this put up a little modal or something. probably later though
    }

    // disable itself until next round
    disableInput();
  };

  Task<String> looper = new Task<String>() {
    @Override
    protected String call() {
      return App.await();
    }

    @Override
    protected void succeeded() {
      super.succeeded();
    }

    @Override
    protected void cancelled() {
      super.cancelled();
      // request to remove from list
      // TODO : a command that does this
    }
  };

  public Pane launch() { return this; }
}
