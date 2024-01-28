package com.zkwd.client.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    // db connection
    private static Connection connection;
    public static Connection getConnection() { return connection; }
    // user data
    private static int uid;
    private static String uname;
    public static void setUserId(int id) { uid = id; }
    public static void setUserName(String name) { uname = name; }
    public static int getUserId() { return uid; }
    public static String getUserName() { return uname; }

    @Override
    public void start(Stage stage) throws IOException {

        StackPane sp = new StackPane(new Text("loading"));

        // connect to server
        hook = new ServerMessenger("localhost", 8888);

        // connect to local db
        try {
            connectAsGuest();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        scene = new Scene(sp, 700, 450);

        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/stylesheet.css").toExternalForm());

        stage.setScene(scene);
        stage.show();

        changeState(AppState.LOGIN);
    }

    @Override
    public void stop() {
        // if we are in game, tell server that we are abandoning the game.
        if(currentState == AppState.INGAME) {
            System.out.println("exiting");
            App.send("exit");
        }

        try {
            disconnect();
        } catch (SQLException e) {
            System.out.println("failed to disconnect from db");
        }

        System.exit(0);
    }

    /**
     * Connect to database as guest.
     * @throws SQLException if failed
     */
    public static void connectAsGuest() throws SQLException {
        System.out.println("connecting to mariadb...");
        connection = DriverManager.getConnection(
            "jdbc:mariadb://localhost:3306/gogame", "guest", ""
        );
    }

    /**
     * Connect to database as guest.
     * @throws SQLException if failed
     */
    public static void connectAsUser() throws SQLException {
        System.out.println("connecting to mariadb...");
        // TODO: MOVE TO RESOURCES!!!
        connection = DriverManager.getConnection(
            "jdbc:mariadb://localhost:3306/gogame", "user", "secret_password"
        );
    }

    /**
     * Disconnect from database.
     * @throws SQLException if failed
     */
    public static void disconnect() throws SQLException {
        System.out.println("disconnecting from mariadb...");
        connection.close();
    }

    /**
     * Changes the state of the application.
     * @param state The desired state.
     */
    public static void changeState(AppState state){
        scene.setRoot(state.getState());
        currentState = state;
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
