package com.zkwd.client.screens.logs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zkwd.client.model.App;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class GameLogScreen extends BorderPane {
  // little sidebar with a scrolling view of the games this user was a part of.
  // get list in constructor, give refresh button?

  // placeholder - choose a game to view it here! or smth like that
  // on the center, put the game view

  private final VBox itemList = new VBox(10);

  public GameLogScreen() {

    refreshList();

    this.setLeft(itemList);
  }

  private final double itemWidth = 120;
  private final double itemHeight = 50;

  private void refreshList() {
    try {
      itemList.getChildren().clear();

      PreparedStatement req = App.getConnection().prepareStatement("""
        SELECT black, white, id
        FROM Games
        WHERE black = ?
        OR white = ?
        ORDER BY timestamp DESC
      """);
      req.setInt(1, App.getUserId());
      req.setInt(2, App.getUserId());

      ResultSet res = req.executeQuery();

      while(res.next()) {
        int gid = res.getInt(3);
        int bid = res.getInt(1);
        int wid = res.getInt(2);

        GameListItem item;
        if (bid == App.getUserId()) {
          // user played black
          item = new GameListItem(gid, "vs " + fetchUsername(wid), itemWidth, itemHeight);
          item.getStyleClass().add("black");
        } else {
          // user played white
          item = new GameListItem(gid, "vs " + fetchUsername(bid), itemWidth, itemHeight);
          item.getStyleClass().add("white");
        }

        // TODO : on click bring up game view
        item.setOnMouseClicked(e -> {
          System.out.println(item.getGameID());
          this.setCenter(new GameInspectView(item.getGameID()));
        });

        itemList.getChildren().add(item);
      }

      if (itemList.getChildren().isEmpty()) {
        Label lbl = new Label("play a game!");
        itemList.getChildren().add(lbl);
      }

      req.close();
    } catch (SQLException e) {
      System.out.println(e.getLocalizedMessage());
    }
  }

  /**
   * Get username from user id.
   * @param uid user id
   * @return username of specified user
   */
  private String fetchUsername(int uid) {
    if (uid < 0) {
      return "CPU";
    } else {
      try {
        PreparedStatement req = App.getConnection().prepareStatement("""
          SELECT username FROM Users
          WHERE id = ?
        """);
        req.setInt(1, uid);
        ResultSet res = req.executeQuery();
        req.close();
        res.next();

        return res.getString(1);
      } catch (SQLException e) {
        System.out.println(e.getLocalizedMessage());
        return "unknown";
      }
    }
  }
}
