package com.zkwd;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * Draws a board to be displayed on screen.
 * 
 * (we could make this a builder for free tbh)
 */
public class GUIBoard extends Group{
    int size;
    Board boardState;
    GridPane gp;

    /**
     * size of circle nodes and padding - will determine size of board
     */
    final double CircleSize = 12;
    final double GridPadding = 3;
    
    public GUIBoard(Board state){
        super();
        this.size = state.getSize();

        // grid lines
        for(int i = 0; i < size; ++i){

            Line vertical = new Line(
                getCoords(i, 0).getX(),
                getCoords(i, 0).getY(),
                getCoords(i, size - 1).getX(),
                getCoords(i, size - 1).getY());

            vertical.setStrokeWidth(2);
            this.getChildren().add(vertical);

            Line horizontal = new Line(
                getCoords(0, i).getX(),
                getCoords(0, i).getY(),
                getCoords(size - 1, i).getX(),
                getCoords(size - 1, i).getY());

            horizontal.setStrokeWidth(2);
            this.getChildren().add(horizontal);
        }

        // stones
        gp = new GridPane();
        //gp.setPadding(new Insets(GridPadding));
        gp.setAlignment(Pos.CENTER);
        for(int i = 0; i < size; ++i){
            for(int j = 0; j < size; ++j){
                Circle shape = new Circle(CircleSize);
                GridPane.setMargin(shape, new Insets(GridPadding));
                shape.setStroke(Color.BLACK);
                shape.setStrokeWidth(2);

                switch(state.getValue(i, j)){
                    case 1: //WHITE
                        shape.setFill(Color.WHITE);
                        break;
                    case -1: //BLACK
                        shape.setFill(Color.BLACK);
                        break;
                    default:
                        shape.setFill(Color.TRANSPARENT);
                        shape.setStroke(Color.TRANSPARENT);
                }
                gp.add(shape, i, j);
            }
        }

        this.getChildren().add(gp);
    }

    Point2D getCoords(int x, int y){
        double w = GridPadding + CircleSize + 1;
        return new Point2D(w * (2*x + 1), w * (2*y + 1));
    }
}
