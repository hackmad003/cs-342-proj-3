package controllers;

import client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import client.PokerInfo;

public class ResultController {
    
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
    
    public void setGameResult(PokerInfo info, int totalWinnings, Client client, GameplayController gameplayController) {
        this.client = client;
        this.gameplayController = gameplayController;
        this.totalWinnings = totalWinnings;
        
        // Set result message
        String result = info.getGameResult();
        switch (result) {
            case "WIN":
                resultLabel.setText("🎉 You Won! 🎉");
                resultLabel.setStyle("-fx-text-fill: green;");
                break;
            case "LOSE":
                resultLabel.setText("You Lost");
                resultLabel.setStyle("-fx-text-fill: red;");
                break;
            case "FOLD":
                resultLabel.setText("You Folded");
                resultLabel.setStyle("-fx-text-fill: orange;");
                break;
            case "PUSH":
                resultLabel.setText("Push");
                resultLabel.setStyle("-fx-text-fill: blue;");
                break;
        }
        
        // Set winnings for this game
        int gameWinnings = info.getTotalWinnings();
        if (gameWinnings >= 0) {
            winningsLabel.setText("Won $" + gameWinnings + " this game");
            winningsLabel.setStyle("-fx-text-fill: green;");
        } else {
            winningsLabel.setText("Lost $" + Math.abs(gameWinnings) + " this game");
            winningsLabel.setStyle("-fx-text-fill: red;");
        }
        
        // Set total winnings
        totalWinningsLabel.setText("Total Winnings: $" + totalWinnings);
        if (totalWinnings >= 0) {
            totalWinningsLabel.setStyle("-fx-text-fill: green;");
        } else {
            totalWinningsLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    @FXML
    private void handlePlayAgain() {
        try {
            // Return to gameplay screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/gameplay.fxml"));
            Parent root = loader.load();
            
            GameplayController controller = loader.getController();
            controller.setClient(client);
            
            // Restore total winnings
            controller.totalWinnings = this.totalWinnings;
            controller.totalWinningsLabel.setText("$" + totalWinnings);
            controller.resetForNewGame();
            
            Stage stage = (Stage) playAgainButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleExit() {
        // Send disconnect message
        PokerInfo info = new PokerInfo();
        info.setMessageType("DISCONNECT");
        client.send(info);
        
        Platform.exit();
        System.exit(0);
    }
}