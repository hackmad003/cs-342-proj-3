package shared;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

////////////////
// CLIENT APP //
////////////////
public class ClientApp extends Application {
    
	
	///////////
	// START //
	///////////
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/welcome.fxml"));
        primaryStage.setTitle("Three Card Poker");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
        
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
    }
    
    //////////
    // MAIN //
    //////////
    public static void main(String[] args) {
        launch(args);
    }
}