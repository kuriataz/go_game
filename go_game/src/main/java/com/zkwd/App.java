package com.zkwd;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

    @Override
    public void start(Stage stage){

        Board board = new Board(10).randomize();

        GUIBoard gb = new GUIBoard(board);

        Text txt = new Text("- board -");
        
        VBox box = new VBox(txt, gb);
        box.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(box, 600, 400));
        stage.show();
    }
}
