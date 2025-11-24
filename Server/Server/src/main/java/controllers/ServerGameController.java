package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import shared.Server;

public class ServerGameController {
    
    @FXML
    private Label serverStatusLabel;
    
    @FXML
    private Label clientCountLabel;
    
    @FXML
    private ListView<String> gameLogList;
    
    @FXML
    private Button stopServerButton;
    
    private Server server;
    
    @FXML
    public void initialize() {
        serverStatusLabel.setText("Server Status: Running");
        clientCountLabel.setText("Connected Clients: 0");
    }
    
    public void setServer(Server server) {
        this.server = server;
    }
    
    public void addLogMessage(String message) {
        Platform.runLater(() -> {
            gameLogList.getItems().add(message);
            // Auto-scroll to bottom
            gameLogList.scrollTo(gameLogList.getItems().size() - 1);
            
            // Update client count if server is available
            if (server != null && server.clients != null) {
                updateClientCount(server.clients.size());
            }
        });
    }
    
    public void updateClientCount(int count) {
        Platform.runLater(() -> {
            clientCountLabel.setText("Connected Clients: " + count);
        });
    }
    
    @FXML
    private void handleStopServer() {
        // Close server
        Platform.exit();
        System.exit(0);
    }
}