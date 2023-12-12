package com.zkwd;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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

        Text txt = new Text("hello world");
        StackPane sp = new StackPane(txt);

        stage.setScene(new Scene(sp, 600, 400));
        stage.show();
    }
}
