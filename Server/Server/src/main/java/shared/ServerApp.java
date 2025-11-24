package shared;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//////////////////////
// SERVER APP CLASS //
//////////////////////
public class ServerApp extends Application {
    
	///////////
	// START //
	///////////
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/server_intro.fxml"));
        primaryStage.setTitle("Three Card Poker - Server");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }
    
    //////////
    // MAIN //
    //////////
    public static void main(String[] args) {
        launch(args);
    }
}