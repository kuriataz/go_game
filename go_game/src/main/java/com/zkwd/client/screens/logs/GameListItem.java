package com.zkwd.client.screens.logs;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class GameListItem extends StackPane{
  
  private final int gameID;

  private final int radius = 12;

  public GameListItem(int gameID, String title, double width, double height) {
    super();
    this.gameID = gameID;

    Rectangle rect = new Rectangle(width, height);
    rect.setArcHeight(radius);
    rect.setArcWidth(radius);

    Label lbl = new Label(title);

    this.getChildren().addAll(rect, lbl);

    this.getStyleClass().add("game-item");
    rect.getStyleClass().add("game-item-fill");
    lbl.getStyleClass().add("game-item-text");
  }

  public int getGameID() {
    return gameID;
  }
}
