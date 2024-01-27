package com.zkwd.client.screens.logs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.zkwd.client.model.App;
import com.zkwd.client.util.GUIBoardBuilder;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

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
      if (ind > 0 && ind < boards.size()) {
        ind--;
        sp.getChildren().set(0, boards.get(ind));
      }
      if (ind > 0 && ind < boards.size()) {
        left.setDisable(false);
      }
      if (ind >= boards.size() - 1) {
        right.setDisable(true);
      }
    });

    this.setCenter(boardBox);

    // center - board
    // left and right - last/next move
    // close btn in corner
    // above board, usernames of players
  }

  private void requestGameInfo(int gameID) {
    // select black, white, history from blah blah
    boards.clear();
    try {
      PreparedStatement req = App.getConnection().prepareStatement("""
        SELECT ub.username, uw.username, g.moves, g.timestamp, g.bsize
        FROM Games g
        JOIN Users ub on g.black = ub.id
        JOIN Users uw on g.white = uw.id
        WHERE id = ?
      """);
      req.setInt(1, gameID);

      ResultSet res = req.executeQuery();
      res.next(); // move to first result

      black = res.getString(1);
      white = res.getString(2);
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
    String board = "";
    for (int i = 0; i < bsize; ++i) {
      for (int j = 0; j < bsize; ++j) {
        board += "E";
      }
      board += "|";
    }

    while(!hist.isEmpty()) {
      char[] move = hist.substring(0, 2).toCharArray();
      hist = hist.substring(3);

      // position is x * bsize + y
      int pos = parseLetter(move[0]) * bsize + parseLetter(move[1]);
      char col = move[2];

      // add new stone to board
      board = board.substring(0, pos) + col + board.substring(pos);

      // generate board display and store
      boards.add(builder.DisplayBoard(board));
    }
  }

  private int parseLetter(char c) {
    return c - 'a';
  }
}
