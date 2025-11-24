package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import shared.Client;


////////////////////////
// WELCOME CONTROLLER //
////////////////////////
public class WelcomeController {
    
	// GUI Objects //
    @FXML
    private TextField ipField;
    
    @FXML
    private TextField portField;
    
    @FXML
    private Button connectButton;
    
    
    ////////////////
    // INITIALIZE //
    ////////////////
    @FXML
    public void initialize() {
        ipField.setText("127.0.0.1");
        portField.setText("5555");
    }
    
    ////////////////////
    // HANDLE CONNECT //
    ////////////////////
    @FXML
    private void handleConnect(ActionEvent event) {
        try {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());
            
            // Switch to gameplay and let it create the client
            switchToGameplay(ip, port);
            
        } catch (NumberFormatException e) {
            showError("Invalid Port", "Please enter a valid port number.");
        } catch (Exception e) {
            showError("Error", "Failed to connect: " + e.getMessage());
        }
    }
    
    
    /////////////////////////
    // SWITCH TO GAME PLAY //
    /////////////////////////
    private void switchToGameplay(String ip, int port) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/gameplay.fxml"));
        Parent root = loader.load();
        
        GameplayController controller = loader.getController();
        
        // Pass IP and port to controller - let it create the client
        controller.connectToServer(ip, port);
        
        Stage stage = (Stage) connectButton.getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 850));
        stage.setTitle("Three Card Poker - Play");
        stage.setMinWidth(1000);
        stage.setMinHeight(800);
        stage.centerOnScreen();

    }
    
    ////////////////
    // SHOW ERROR //
    ////////////////
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}