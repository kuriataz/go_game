package com.zkwd.client.model;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The starting point of the client application.
 */
public class App extends Application
{
    /**
     * Holds the current scene. Is important
     */
    private static Scene scene;

    @Override
    public void start(Stage stage) {

        StackPane sp = new StackPane(new Text("loading"));

        scene = new Scene(sp, 600, 400);

        stage.setScene(scene);
        stage.show();

        changeState(State.LOBBY);
    }

    /**
     * Changes the state of the application.
     * @param state The desired state.
     */
    public static void changeState(State state){
        scene.setRoot(state.getState().launch());
    }

    public static void main( String[] args )
    {
        launch(args);
    }
}
