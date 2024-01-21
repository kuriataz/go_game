package com.zkwd.client.model;

import java.io.IOException;

import com.zkwd.client.ServerMessenger;

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
    private static AppState currentState;

    private static ServerMessenger hook;

    @Override
    public void start(Stage stage) throws IOException {

        StackPane sp = new StackPane(new Text("loading"));

        //connect to server
        hook = new ServerMessenger("localhost", 8888);

        scene = new Scene(sp, 600, 400);

        stage.setScene(scene);
        stage.show();

        changeState(AppState.LOBBY);
    }

    @Override
    public void stop() {

        // if we are in game, tell server that we are abandoning the game.
        if(currentState == AppState.INGAME) {
            App.send("-2 0");
        }

        System.exit(0);
    }

    /**
     * Changes the state of the application.
     * @param state The desired state.
     */
    public static void changeState(AppState state){
        scene.setRoot(state.getState());
    }

    public static ServerMessenger getServerHook() {
        return hook;
    }

    /**
     * Send a message to the server and wait for a response.
     * @param message
     * @return The response
     */
    public static String transmit(String message){
        return hook.transmit(message);
    }

    public static void send(String message){
        hook.send(message);
    }

    /**
     * Wait for a message from the server.
     * @return The message, once it is delivered.
     */
    public static String await() {
        return hook.await();
    }

    public static void main( String[] args )
    {
        launch(args);
    }
}
