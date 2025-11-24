package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import shared.PokerInfo;
import shared.Client;


/////////////////////////////
// RESULT CONTROLLER CLASS //
/////////////////////////////

public class ResultController {
    
    
    // GUI OBJECTS //
    @FXML
    private Label resultLabel;
    
    @FXML
    private Label winningsLabel;
    
    @FXML
    private Label totalWinningsLabel;
    
    @FXML
    private Button playAgainButton;
    
    @FXML
    private Button exitButton;
    
    private Client client;
    private GameplayController gameplayController;
    private int totalWinnings;
    
    
//////////////////////
//SET GAME RESULTS //
//////////////////////
public void setGameResult(PokerInfo info, int totalWinnings, Client client, GameplayController gameplayController) {
this.client = client;
this.gameplayController = gameplayController;
this.totalWinnings = totalWinnings;

// Set result message with CSS classes
String result = info.getGameResult();
resultLabel.getStyleClass().clear();

switch (result) {
case "WIN":
resultLabel.setText(" You Won! ");
resultLabel.getStyleClass().add("result-win");
break;
case "LOSE":
resultLabel.setText("You Lost");
resultLabel.getStyleClass().add("result-lose");
break;
case "FOLD":
resultLabel.setText("You Folded");
resultLabel.getStyleClass().add("result-fold");
break;
case "PUSH":
resultLabel.setText("Push");
resultLabel.getStyleClass().add("result-push");
break;
}

// Set winnings for this game with CSS classes
int gameWinnings = info.getTotalWinnings();
winningsLabel.getStyleClass().clear();

if (gameWinnings >= 0) {
winningsLabel.setText("Won $" + gameWinnings + " this game");
winningsLabel.getStyleClass().add("winnings-positive");
} else {
winningsLabel.setText("Lost $" + Math.abs(gameWinnings) + " this game");
winningsLabel.getStyleClass().add("winnings-negative");
}

// Set total winnings with CSS classes
totalWinningsLabel.setText("Total Winnings: $" + totalWinnings);
totalWinningsLabel.getStyleClass().clear();

if (totalWinnings >= 0) {
totalWinningsLabel.getStyleClass().add("total-winnings-positive");
} else {
totalWinningsLabel.getStyleClass().add("total-winnings-negative");
}
}
    
    
    ///////////////////////
    // HANDLE PLAY AGAIN //
    ///////////////////////
    @FXML
    private void handlePlayAgain() {
        try {
            // Load the gameplay FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/gameplay.fxml"));
            Parent root = loader.load();
            
            // Get the NEW controller that was created by FXML loading
            GameplayController newController = loader.getController();
            
            // Set the SAME client 
            newController.setClient(client);
            
            client.setCallback(data -> {
                Platform.runLater(() -> {
                    if (data instanceof String) {
                        String message = (String) data;
                        newController.showMessage(message);
                        
                        if (message.contains("Cannot connect") || 
                            message.contains("Connection to server lost")) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Connection Error");
                            alert.setContentText(message);
                            alert.showAndWait();
                        }
                    } else if (data instanceof PokerInfo) {
                        newController.handleServerMessage((PokerInfo) data);
                    }
                });
            });
            
            // total winnings
            newController.totalWinnings = this.totalWinnings;
            newController.totalWinningsLabel.setText("$" + totalWinnings);
            
            // Reset for new game
            newController.resetForNewGame();
            
            // Show the gameplay scene
            Stage stage = (Stage) playAgainButton.getScene().getWindow();
            
            Scene scene = new Scene(root, 1200, 850);
            stage.setScene(scene);
            stage.setTitle("Three Card Poker - Play");
            
            stage.setMinWidth(1000);
            stage.setMinHeight(800);
            
            stage.centerOnScreen();
            
            //System.out.println("DEBUG: Returned to gameplay screen (1200x850, centered)");
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to return to gameplay: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to start new game: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    /////////////////
    // HANDLE EXIT //
    /////////////////
    @FXML
    private void handleExit() {
        // Send disconnect message
        if (client != null) {
            PokerInfo info = new PokerInfo();
            info.setMessageType("DISCONNECT");
            client.send(info);
        }
        
        Platform.exit();
        System.exit(0);
    }
    
} //EOC