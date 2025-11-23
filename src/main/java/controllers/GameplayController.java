package controllers;

import client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import client.Card;
import client.PokerInfo;

import java.util.ArrayList;

public class GameplayController {
    
    @FXML private TextField anteField;
    @FXML private TextField pairPlusField;
    @FXML private Button dealButton;
    @FXML private Button playButton;
    @FXML private Button foldButton;
    
    @FXML private Label playerCard1;
    @FXML private Label playerCard2;
    @FXML private Label playerCard3;
    @FXML private Label dealerCard1;
    @FXML private Label dealerCard2;
    @FXML private Label dealerCard3;
    
    @FXML private Label anteBetLabel;
    @FXML private Label pairPlusBetLabel;
    @FXML private Label playBetLabel;
    @FXML public Label totalWinningsLabel;
    @FXML private TextArea gameInfoArea;
    
    @FXML private MenuBar menuBar;
    
    private Client client;
    private PokerInfo currentGameInfo;
    public int totalWinnings = 0;
    private boolean newLookEnabled = false;
    
    @FXML
    public void initialize() {
        playButton.setDisable(true);
        foldButton.setDisable(true);
        
        anteField.setText("5");
        pairPlusField.setText("0");
        
        updateDisplays();
        clearCards();
    }
    
    // NEW METHOD - Create client with proper callback
    public void connectToServer(String ip, int port) {
        client = new Client(data -> {
            Platform.runLater(() -> {
                if (data instanceof String) {
                    String message = (String) data;
                    showMessage(message);
                    
                    if (message.contains("Cannot connect") || message.contains("Connection to server lost")) {
                        showError("Connection Error", message);
                    }
                } else if (data instanceof PokerInfo) {
                    handleServerMessage((PokerInfo) data);
                }
            });
        }, ip, port);
        
        client.start();
    }
    
    // OLD METHOD - For when returning from result screen
    public void setClient(Client client) {
        this.client = client;
    }
    
    private void handleServerMessage(PokerInfo info) {
        String msgType = info.getMessageType();
        
        switch (msgType) {
            case "DEAL_CARDS":
                handleDealCards(info);
                break;
            case "GAME_RESULT":
                handleGameResult(info);
                break;
        }
    }
    
    @FXML
    private void handleDeal() {
        try {
            int ante = Integer.parseInt(anteField.getText());
            int pairPlus = Integer.parseInt(pairPlusField.getText());
            
            // Validate bets
            if (ante < 5 || ante > 25) {
                showMessage("Ante bet must be between $5 and $25");
                return;
            }
            
            if (pairPlus != 0 && (pairPlus < 5 || pairPlus > 25)) {
                showMessage("Pair Plus bet must be 0, or between $5 and $25");
                return;
            }
            
            // Send betting info to server
            PokerInfo info = new PokerInfo();
            info.setMessageType("PLACE_BETS");
            info.setAnteBet(ante);
            info.setPairPlusBet(pairPlus);
            
            client.send(info);
            
            dealButton.setDisable(true);
            anteField.setDisable(true);
            pairPlusField.setDisable(true);
            
            showMessage("Bets placed! Dealing cards...");
            
        } catch (NumberFormatException e) {
            showMessage("Please enter valid bet amounts");
        }
    }
    
    private void handleDealCards(PokerInfo info) {
        currentGameInfo = info;
        
        // Display player cards
        ArrayList<Card> playerHand = info.getPlayerHand();
        if (playerHand != null && playerHand.size() == 3) {
            playerCard1.setText(playerHand.get(0).toShortString());
            playerCard2.setText(playerHand.get(1).toShortString());
            playerCard3.setText(playerHand.get(2).toShortString());
        }
        
        // Hide dealer cards
        dealerCard1.setText("?");
        dealerCard2.setText("?");
        dealerCard3.setText("?");
        
        // Update bet displays
        anteBetLabel.setText("$" + info.getAnteBet());
        pairPlusBetLabel.setText("$" + info.getPairPlusBet());
        playBetLabel.setText("$0");
        
        // Enable play/fold buttons
        playButton.setDisable(false);
        foldButton.setDisable(false);
        
        if (info.getMessage() != null) {
            showMessage(info.getMessage());
        }
    }
    
    @FXML
    private void handlePlay() {
        int playBet = currentGameInfo.getAnteBet();
        playBetLabel.setText("$" + playBet);
        
        currentGameInfo.setMessageType("PLAYER_DECISION");
        currentGameInfo.setPlayerFolded(false);
        currentGameInfo.setPlayBet(playBet);
        
        client.send(currentGameInfo);
        
        playButton.setDisable(true);
        foldButton.setDisable(true);
        
        showMessage("Playing! Waiting for results...");
    }
    
    @FXML
    private void handleFold() {
        currentGameInfo.setMessageType("PLAYER_DECISION");
        currentGameInfo.setPlayerFolded(true);
        
        client.send(currentGameInfo);
        
        playButton.setDisable(true);
        foldButton.setDisable(true);
        
        showMessage("Folded.");
    }
    
    private void handleGameResult(PokerInfo info) {
        // Show dealer cards
        ArrayList<Card> dealerHand = info.getDealerHand();
        if (dealerHand != null && dealerHand.size() == 3) {
            dealerCard1.setText(dealerHand.get(0).toShortString());
            dealerCard2.setText(dealerHand.get(1).toShortString());
            dealerCard3.setText(dealerHand.get(2).toShortString());
        }
        
        // Update total winnings
        totalWinnings += info.getTotalWinnings();
        totalWinningsLabel.setText("$" + totalWinnings);
        
        if (info.getMessage() != null) {
            showMessage(info.getMessage());
        }
        
        // Wait a moment then show result screen
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Give player time to see cards
                Platform.runLater(() -> {
                    try {
                        showResultScreen(info);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void showResultScreen(PokerInfo info) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/result.fxml"));
        Parent root = loader.load();
        
        ResultController controller = loader.getController();
        controller.setGameResult(info, totalWinnings, client, this);
        
        Stage stage = (Stage) dealButton.getScene().getWindow();
        stage.setScene(new Scene(root, 800, 600));
    }
    
    public void resetForNewGame() {
        dealButton.setDisable(false);
        anteField.setDisable(false);
        pairPlusField.setDisable(false);
        playButton.setDisable(true);
        foldButton.setDisable(true);
        
        anteField.setText("5");
        pairPlusField.setText("0");
        playBetLabel.setText("$0");
        
        clearCards();
        gameInfoArea.clear();
    }
    
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
    
    @FXML
    private void handleFreshStart() {
        totalWinnings = 0;
        totalWinningsLabel.setText("$0");
        resetForNewGame();
        showMessage("Fresh start! Total winnings reset to $0");
    }
    
    @FXML
    private void handleNewLook() {
        newLookEnabled = !newLookEnabled;
        
        Scene scene = dealButton.getScene();
        scene.getStylesheets().clear();
        
        if (newLookEnabled) {
            // Apply alternate stylesheet
            scene.getStylesheets().add(getClass().getResource("/styles/styles2.css").toExternalForm());
        } else {
            // Apply default stylesheet
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        }
    }
    
    private void showMessage(String message) {
        gameInfoArea.appendText(message + "\n");
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void updateDisplays() {
        totalWinningsLabel.setText("$" + totalWinnings);
    }
    
    private void clearCards() {
        playerCard1.setText("");
        playerCard2.setText("");
        playerCard3.setText("");
        dealerCard1.setText("");
        dealerCard2.setText("");
        dealerCard3.setText("");
    }
}