// package com.zkwd;

// import com.zkwd.server.game.exceptions.MoveException;
// import com.zkwd.server.game.gamestate.Board;

// public class CaptureTest {
//   public void testCaptureStone() {
//     Board b = new Board(9);
//     b = b.setBoard("EWEEEEEBW|EWEEEEEEE|EEEEEEEEE|EEEEEEEEE|EEEEBEEEE|"
//                    + "EEEBWBEEE|EEEEEEEEE|EEEEEEEEE|EEEEEEEEE|");

//     try {
//       b.putStone(1, 8, -1);
//     } catch (MoveException e) {
//       e.printStackTrace();
//     }

//     assert (b.board[0][8].getState() == 0);
//     try {
//       b.putStone(6, 4, -1);
//     } catch (MoveException e) {
//       e.printStackTrace();
//     }
//     assert (b.board[5][4].getState() == 0);
//   }
//   public void testCaptureChain() {
//     Board b = new Board(9);
//     b = b.setBoard("BWEEEEEBE|BWEEEEEEB|EWEEEBEEE|EEEEBWBEE|EEEEBWEEE|" +
//                    "EEEBEBEEE|WEEEBEEEE|WEEEEEEEE|WEEEEEEEE|");

//     try {
//       b.putStone(0, 2, 1);
//     } catch (MoveException e) {
//       e.printStackTrace();
//     }

//     assert (b.board[0][0].getState() == 0);
//     assert (b.board[0][1].getState() == 0);
//     try {
//       b.putStone(4, 6, -1);
//     } catch (MoveException e) {
//       e.printStackTrace();
//     }
//     assert (b.board[4][5].getState() == 0);
//     assert (b.board[3][5].getState() == 0);
//   }
// }
