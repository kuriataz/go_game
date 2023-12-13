package com.zkwd.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Hello world!
 * Now with JavaFX (functional, even!)
 */
public class App extends Application
{
    public static void main( String[] args )
    {
        launch(args);
    }

    private Board board;
    private GUIBoard gb;
    private VBox box;
    private Player p;
    private Text txt;

    @Override
    public void start(Stage stage) throws IOException{

        p = new Player("localhost", 8888);

        board = new Board(10).randomize();

        gb = new GUIBoard(board);

        //TextField tf = new TextField();
        txt = new Text("- board -");
        
        box = new VBox(txt, gb);
        box.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(box, 600, 400));
        stage.show();

        makeClickable();
    }

    void makeClickable() {
        // allow intersections to be clicked
        for(Node circle : gb.getButtons()) {
            circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent event){
                    //get location
                    int x = GridPane.getRowIndex(circle);
                    int y = GridPane.getColumnIndex(circle);
                    String out = p.transmit("(" + x + " " + y + ")");
                    if(out != null){
                        txt.setText(out);

                        board.flip(x, y);
                        
                        box.getChildren().remove(gb);
                        gb = new GUIBoard(board);
                        box.getChildren().add(gb);

                        makeClickable();
                    } else {
                        txt.setText("something went wrong");
                    }
                }
            });
        }
    }
}
