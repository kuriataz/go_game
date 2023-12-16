package com.zkwd.client.screens.ingame;

import com.zkwd.client.model.App;
import com.zkwd.client.model.IScreen;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class GameScreen extends BorderPane implements IScreen {

  GUIBoardBuilder boardBuilder;

  Group board;
  Text txt = new Text("default text");

  public GameScreen() {
    super();

    this.setTop(txt);
    txt.setTextAlignment(TextAlignment.CENTER);

    this.boardBuilder = new GUIBoardBuilder();

    // begin game
    // somehow
    runGame();
  }

  private void updateBoard(String boardString){
    board = boardBuilder.DisplayBoard(boardString);
    this.setCenter(board);
  }

  /**
   * I think im putting the game loop in here so its clearly distinguishable from the setup.
   * @return True if this player has won the game.
   */
  private boolean runGame() {

    // loop needs to run in a background thread so as to not freeze the application.
    new Thread() {
      /**
       * TODO : think about when the loop should end?
       */
      public void run() {
        String message;
        String s = App.await();

        String clr = s.split("_")[1];
        String brd = s.split("_")[2];

        Platform.runLater(() -> {
          updateBoard(brd);
          txt.setText(clr);
        });

        while(true){
          // wait for your round
          do {
            message = App.await();
          } while(!message.equals("game_go") && !message.equals("game_goagain"));

          if(message.equals("game_go")){

            // next signal will be game_[round]_[boardState]
            String nboard = App.await();
            String[] split = nboard.split("_");

            Platform.runLater(() -> {
              // TODO : update round with split[1]
              updateBoard(split[2]);
            });
          }

          Platform.runLater(() -> {
            enableInput();
          });

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
           * 
           * I Am Not Sure This Works Lol
           */
        }
      }
    }.start();

    return true;
  }

  private void enableInput() {
    System.out.println("awaiting user input");
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
    System.out.println("event");
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
      updateBoard(result);
    } else {
      // TODO : make this put up a little modal or something. probably later though
    }

    // disable itself until next round
    disableInput();
  };

  public Pane launch() { return this; }
}
