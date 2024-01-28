package com.zkwd.client.screens.logs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.zkwd.client.model.App;
import com.zkwd.client.model.Queries;
import com.zkwd.client.util.GUIBoardBuilder;
import com.zkwd.server.game.exceptions.MoveException;
import com.zkwd.server.game.gamestate.Board;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameInspectView extends BorderPane {

  private int ind = 0;

  // game information
  private String white;
  private String black;
  private String hist;
  private String time;
  private int bsize;
  // board list
  private final ArrayList<Group> boards = new ArrayList<>();

  private final GUIBoardBuilder builder = new GUIBoardBuilder();

  public GameInspectView(int gameID) {
    super();

    requestGameInfo(gameID);

    Button left = new Button("<");
    Button right = new Button(">");
    left.setDisable(true);
    if (boards.isEmpty()) {
      right.setDisable(true);
    }

    StackPane sp = new StackPane(boards.get(ind));

    HBox boardBox = new HBox(10, left, sp, right);
    boardBox.setAlignment(Pos.CENTER);

    Label timestamp = new Label("played at: " + time);

    VBox box = new VBox(15, boardBox, timestamp);
    box.setAlignment(Pos.CENTER);

    // left button functionality
    left.setOnAction(e -> {
      if (ind > 0 && ind < boards.size()) {
        ind--;
        sp.getChildren().set(0, boards.get(ind));
      }
      if (ind == 0) {
        left.setDisable(true);
      }
      if (ind < boards.size() - 1) {
        right.setDisable(false);
      }
    });

    // right button functionality
    right.setOnAction(e -> {
      if (ind >= 0 && ind < boards.size() - 1) {
        ind++;
        sp.getChildren().set(0, boards.get(ind));
      }
      if (ind > 0 && ind < boards.size()) {
        left.setDisable(false);
      }
      if (ind >= boards.size() - 1) {
        right.setDisable(true);
      }
      System.out.println(ind + "|" + boards.size());
    });

    this.setCenter(box);

    // center - board
    // left and right - last/next move
    // close btn in corner
    // above board, usernames of players
  }

  private void requestGameInfo(int gameID) {
    // select black, white, history from blah blah
    boards.clear();
    System.out.println(boards.size());
    try {
      PreparedStatement req = App.getConnection().prepareStatement("""
        SELECT black, white, moves, timestamp, boardsize
        FROM Games
        WHERE id = ?
      """);
      req.setInt(1, gameID);

      ResultSet res = req.executeQuery();
      res.next(); // move to first result

      black = Queries.fetchUsername(res.getInt(1));
      white = Queries.fetchUsername(res.getInt(2));
      hist = res.getString(3);
      time = res.getString(4);
      bsize = res.getInt(5);

      req.close();
    } catch (SQLException e) {
      System.out.println(e.getLocalizedMessage());
      return;
    }
    
    // create a new boardstring
    // and then iterate through moves
    // by placing the appropriate stone
    // in the correct place
    // and creating a new board
    // and putting it in the array
    // String board = "";
    // for (int i = 0; i < bsize; ++i) {
    //   for (int j = 0; j < bsize; ++j) {
    //     board += "E";
    //   }
    //   board += "|";
    // }

    // 9 -> 12
    // 19 -> 5
    double csize = Math.round(18.0 - 2.0 * bsize / 3.0);
    // 9 -> 3
    // 19 -> 1
    double gpadd = Math.round(5.0 - 2.0 * bsize / 9.0);

    Board b = new Board(bsize);

    boards.add(builder.customBoard(b.prepareBoardString(), csize, gpadd));

    while(!hist.isEmpty()) {
      char[] move = hist.substring(0, 3).toCharArray();
      hist = hist.substring(3);

      // position is x * bsize + y
      // int pos = parseLetter(move[1]) * (bsize + 1) + parseLetter(move[2]);
      // char col = move[0];

      // // insert new stone into string
      // board = board.substring(0, pos) + col + board.substring(pos + 1);

      // System.out.println(hist);
      int pcol = move[0] == 'W' ? 1 : -1;
      int x = parseLetter(move[1]);
      int y = parseLetter(move[2]);

      try {
        b.putStone(x, y, pcol);
        b.removeCapturedStones();
        b.removeCapturedChains();
      } catch (MoveException e) {
        // never thrown - move should always be correct
        System.out.println("???");
      }

      // generate board display and store
      boards.add(builder.customBoard(b.prepareBoardString(), csize, gpadd));
    }
  }

  private int parseLetter(char c) {
    return c - 'a';
  }
}
