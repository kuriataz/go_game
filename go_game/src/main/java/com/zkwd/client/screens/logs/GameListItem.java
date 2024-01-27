package com.zkwd.client.screens.logs;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
    rect.setFill(Color.GAINSBORO);

    Label lbl = new Label(title);

    this.getChildren().addAll(rect, lbl);
  }

  public int getGameID() {
    return gameID;
  }
}
