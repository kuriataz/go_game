package com.zkwd.server.game;

import com.zkwd.client.Player;

/**
 * Master class for a singular game of Go.
 */
public class GoGame {
  private Player black;
  private Player white;
  private Board board;

  private int round = 0;
  private boolean turn = true; //turn is true when black goes, false when white goes

  public void run(){
    Player currentPlayer = black;

    //await both players to be ready
    //after that, assume 

    while(true){
      //player makes move
      String move = currentPlayer.waitForMove();
      //move format is "x y"
      try{
        int x = Integer.parseInt(move.split(" ")[0]);
        int y = Integer.parseInt(move.split(" ")[1]);

        //check move for correctness
        boolean correct = true;

        if(correct){

          //if move is correct, update board
          //and calculate differences
          if(currentPlayer == black){
            board.putBlack(x, y);
            currentPlayer = white;
          } else {
            board.putWhite(x, y);
            currentPlayer = black;
          }

          //send correct signal: the new board to display
          String newBoard = board.prepareBoardString();
          white.send(newBoard);
          black.send(newBoard);

          round++;
        } else {
          //send incorrect signal - current player must go again
          currentPlayer.send("");
        }
      } catch (NumberFormatException e){
        // the transmitted move was somehow incorrect - current player must try again
        currentPlayer.send("");
      }
    }
  }

  String getMove() {
    return "";
  }
}
