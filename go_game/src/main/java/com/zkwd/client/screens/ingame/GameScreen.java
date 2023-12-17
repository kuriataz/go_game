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
  Text txt = new Text("default text");

  MessageLog log = new MessageLog();
  
  TextField input = new TextField();
  Button btn = new Button("send");

  HBox hbox = new HBox(input, btn);
  VBox vbox = new VBox(log, hbox);

  public GameScreen() {
    super();

    this.setTop(txt);
    BorderPane.setAlignment(txt, Pos.CENTER);

    this.setCenter(vbox);
    hbox.setAlignment(Pos.CENTER);
    vbox.setAlignment(Pos.CENTER);

    input.setDisable(true);
    btn.setDisable(true);

    btn.setOnMouseClicked(clickHandler);

    this.boardBuilder = new GUIBoardBuilder();

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
          // output.setText("type a message and send it to the other player!");
        } else {
          txt.setText("white pieces");
          // output.setText("");
          String oppUpdate = App.await();

          Platform.runLater(() -> {
            log.push(oppUpdate);
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
            Platform.runLater(() -> {
              input.setDisable(false);
              btn.setDisable(false);
            });

            verdict = App.await();
          } while (!verdict.equals("game_correct"));

          // update text
          String update = App.await();

          Platform.runLater(() -> {
            log.push(update);
          });

          String oppUpdate = App.await();

          Platform.runLater(() -> {
            log.push(oppUpdate);
          });

          // click handler handles sending messages
        }

        // String message;
        // String s = App.await();

        // String clr = s.split("_")[1];
        // String brd = s.split("_")[2];

        // Platform.runLater(() -> {
        //   updateBoard(brd);
        //   txt.setText(clr);
        // });

        // while(true){
        //   // wait for your round
        //   do {
        //     message = App.await();
        //   } while(!message.equals("game_go") && !message.equals("game_goagain"));

        //   if(message.equals("game_go")){

        //     // next signal will be game_[round]_[boardState]
        //     String nboard = App.await();
        //     String[] split = nboard.split("_");

        //     Platform.runLater(() -> {
        //       // TODO : update round with split[1]
        //       updateBoard(split[2]);
        //     });
        //   }

        //   Platform.runLater(() -> {
        //     enableInput();
        //   });

        //   /**
        //    * App proceeds to the next loop iteration and does nothing until it receives a signal to move again ("game_go"
        //    * or "game_goagain")
        //    * In that time, a message is only sent to the server if the player clicks on the board.
        //    * When the player clicks on the board, the app requests to make the move and waits for the server to
        //    * tell it whether the move is valid or not.
        //    * If the move is valid, the server moves on to ask the other player.
        //    * Otherwise, the server gives a "game_goagain" signal to the app, which causes it to move again,
        //    * but without updating the board this time.
        //    * 
        //    * Also, all messages received while waiting for "game_go" or "go_again" are ignored.
        //    * 
        //    * I Am Not Sure This Works Lol
        //    */
        // }
      }
    }.start();

    return true;
  }

  // private void updateBoard(String boardString){
  //   board = boardBuilder.DisplayBoard(boardString);
  //   this.setCenter(board);
  // }

  // private void enableInput() {
  //   System.out.println("awaiting user input");
  //   board.setOnMouseClicked(clickHandler);
  // }

  // // not sure this is the proper way to do this
  // private void disableInput() {
  //   board.setOnMouseClicked(null);
  // }

  /**
   * X and Y are in local space, so the handler should be applied to the entire board to calculate properly.
   */
  EventHandler<MouseEvent> clickHandler = event -> {
    String msg = input.getText();

    input.setDisable(true);
    input.clear();
    btn.setDisable(true);

    App.send(msg);

    // System.out.println("event");
    // double mouseX = event.getX();
    // double mouseY = event.getY();

    // double circleSize = boardBuilder.CircleSize;
    // double gridPadding = boardBuilder.GridPadding;

    // int clickedX = (int)((mouseX - gridPadding) / (2 * circleSize + 1));
    // int clickedY = (int)((mouseY - gridPadding) / (2 * circleSize + 1));

    // // Convert the coordinates to a string format and transmit it
    // String clickedPosition = clickedX + " " + clickedY;

    // // ask if move is correct
    // String result = App.transmit("move:" + clickedPosition);

    // if(!result.equals("game_incorrect")){
    //   updateBoard(result);
    // } else {
    //   // TODO : make this put up a little modal or something. probably later though
    // }

    // // disable itself until next round
    // disableInput();
  };

  public Pane launch() { return this; }
}
