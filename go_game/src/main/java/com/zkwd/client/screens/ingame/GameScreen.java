package com.zkwd.client.screens.ingame;

import com.zkwd.client.model.App;
import com.zkwd.client.model.IScreen;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class GameScreen extends BorderPane implements IScreen {

  GUIBoardBuilder boardBuilder;

  // autoupdate GUI with changing boardState
  private StringProperty boardState;

  Group board;

  public GameScreen() {
    super();

    boardState.addListener((ChangeListener<String>)
      (obs, newVal, oldVal) -> {
        board = boardBuilder.DisplayBoard(newVal);
        this.setCenter(board);
      }
    );

    // this.setCenter(txt);
    this.boardBuilder = new GUIBoardBuilder();
    this.boardState.set("WEE|BEW|EEE"); // for test
  }

  /**
   * I think im putting the game loop in here so its clearly distinguishable from the setup.
   * @return True if this player has won the game.
   */
  private boolean runGame() {

    String message = App.await();

    /**
     * Game loop proper: make a move, and then wait another move.
     * TODO : think about when the loop should end
     */
    while(true){
      // wait for your round
      while(!message.equals("game_go")){
        message = App.await();
      }

      // next signal will be new board state
      boardState.set(App.await());

      // enable input
      enableInput();

      // wait until an input is sent (and the board changes again)
      // TODO : i dont think this is the best way to do this, especially given the board has just changed before this is invoked. seems dangerous.
      try {
        boardState.wait();
      } catch (InterruptedException e){
        e.printStackTrace();
      }

      // disable input
      disableInput();
    }
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
      }

      // disable itself until next round
      disableInput();
    };

  public Pane launch() { return this; }
}
