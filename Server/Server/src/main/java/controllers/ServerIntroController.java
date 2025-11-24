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
import shared.Server;

///////////////////////////////////
// SERVER INTRO CONTROLLER CLASS //
///////////////////////////////////
public class ServerIntroController {
    
    @FXML
    private TextField portField;
    
    @FXML
    private Button startButton;
    
    ////////////////
    // INITIALIZE //
    ////////////////
    @FXML
    public void initialize() {
        portField.setText("5555"); // Default port
    }
    
    /////////////////////////
    // HANDLE START SERVER //
    /////////////////////////
    @FXML
    private void handleStartServer(ActionEvent event) {
        try {
            int port = Integer.parseInt(portField.getText());
            
            // Validate port range
            if (port < 1024 || port > 65535) {
                showError("Invalid Port", "Port must be between 1024 and 65535.");
                return;
            }
            
            // Switch to game scene first
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/server_game.fxml"));
            Parent root = loader.load();
            
            ServerGameController controller = loader.getController();
            
            Server server = new Server(data -> {
                Platform.runLater(() -> {
                    controller.addLogMessage(data.toString());
                });
            }, port);  
            
            controller.setServer(server);
            
            // Show the game scene
            Stage stage = (Stage) startButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Three Card Poker Server - Running on Port " + port);
            
        } catch (NumberFormatException e) {
            showError("Invalid Port", "Please enter a valid port number.");
        } catch (Exception e) {
            showError("Server Error", "Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
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
    
} //EOC