// package com.zkwd.server.game;

// import com.zkwd.client.Player;
// import java.util.ArrayList;

// /**
//  * Master class for a singular game of Go.
//  */
// public class GoGame {

//   public static final int BLACK = -1;
//   public static final int WHITE = 1;
//   public static final int FREE = 0;

//   private Player black;
//   private ArrayList<Chain> blackChains;

//   private Player white;
//   private ArrayList<Chain> whiteChains;

//   private Board board;

//   private int round = 0;
//   //   private boolean turn = true; //turn is true when black goes, false
//   //   when white goes
//   private int turn = BLACK; // turn is -1 when black goes, 1 when white goes

//   public void run() {
//     Player currentPlayer = black;

//     // await both players to be ready
//     // after that, assume

//     while (true) {
//       // player makes move or passes
//       String move = currentPlayer.waitForMove();
//       // pass
//       if (move == "") {
//         if (currentPlayer == black) {
//           currentPlayer = white;
//         } else {
//           currentPlayer = black;
//         }
//       } else {
//         // move format is "x y"
//         try {
//           int x = Integer.parseInt(move.split(" ")[0]);
//           int y = Integer.parseInt(move.split(" ")[1]);

//           // check move for correctness
//           boolean correct = board.correctMove(x, y, turn);

//           if (correct) {

//             // if move is correct, update board
//             // and calculate differences
//             //   if (turn == BLACK) {
//             //     board.putBlack(x, y);
//             //     turn = WHITE;
//             //   } else {
//             //     board.putWhite(x, y);
//             //     turn = BLACK;
//             //   }
//             if (currentPlayer == black) {
//               board.putBlack(x, y);
//               currentPlayer = white;
//               turn = WHITE;
//             } else {
//               board.putWhite(x, y);
//               currentPlayer = black;
//               turn = BLACK;
//             }

//             // send correct signal: the new board to display
//             String newBoard = board.prepareBoardString();
//             white.send(newBoard);
//             black.send(newBoard);

//             round++;
//           } else {
//             // send incorrect signal - current player must go again
//             currentPlayer.send("");
//           }
//         } catch (NumberFormatException e) {
//           // the transmitted move was somehow incorrect - current player must
//           // try again
//           currentPlayer.send("");
//         }
//       }
//     }
//   }

//   String getMove() { return ""; }
// }
