package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.util.ArrayList;

import shared.Card;
import shared.PokerInfo;
import shared.Client;

///////////////////////////////
// GAMEPLAY CONTROLLER CLASS //
///////////////////////////////
public class GameplayController{
    
    // GUI OBJECTS //
	@FXML public BorderPane rootPane;

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
    private boolean gameInProgress = false;

    
    ///////////////
    // INITALIZE //
    ///////////////
    @FXML
    public void initialize() {
        playButton.setDisable(true);
        foldButton.setDisable(true);
        
        anteField.setText("5");
        pairPlusField.setText("0");
        
        updateDisplays();
        clearCards();
    }
    
    ///////////////////////
    // CONNECT TO SERVER //
    ///////////////////////
    public void connectToServer(String ip, int port) {
        //System.out.println("DEBGU: Connecting to " + ip + ":" + port);
        
        client = new Client(data -> {
            Platform.runLater(() -> {
                //System.out.println("DEBUG: Received data from server: " + data);
                
                if (data instanceof String) {
                    String message = (String) data;
                    showMessage(message);
                    
                    if (message.contains("Cannot connect") || message.contains("Connection to server lost")) {
                        showError("Connection Error", message);
                    }
                } else if (data instanceof PokerInfo) {
                    //System.out.println("DEBUG: Received PokerInfo with messageType: " + ((PokerInfo) data).getMessageType());
                    handleServerMessage((PokerInfo) data);
                }
            });
        }, ip, port);
        
        client.start();
    }
    
    ////////////////
    // SET CLIENT //
    ////////////////
    public void setClient(Client client) {
        this.client = client;
    }
    
    ///////////////////////////
    // HANDLE SERVER MESSAGE //
    ///////////////////////////
    public void handleServerMessage(PokerInfo info) {
        String msgType = info.getMessageType();
        //System.out.println("DEBUG: Handling server message: " + msgType);
        
        switch (msgType) {
            case "DEAL_CARDS":
                handleDealCards(info);
                break;
            case "GAME_RESULT":
                handleGameResult(info);
                break;
            default:
                //System.out.println("DEBUG: Unknown message type: " + msgType);
        }
    }
    
    /////////////////
    // HANDLE DEAL //
    /////////////////
    @FXML
    public void handleDeal() {
        //System.out.println("DEBUG: handleDeal() called");
        
        try {
            int ante = Integer.parseInt(anteField.getText());
            int pairPlus = Integer.parseInt(pairPlusField.getText());
            
            //System.out.println("DEBUG: Ante=" + ante + ", PairPlus=" + pairPlus);
            
            // Validate bets
            if (ante < 5 || ante > 25) {
                showMessage("Ante bet must be between $5 and $25");
                return;
            }
            
            if (pairPlus != 0 && (pairPlus < 5 || pairPlus > 25)) {
                showMessage("Pair Plus bet must be 0, or between $5 and $25");
                return;
            }
            
            PokerInfo info = new PokerInfo();
            info.setMessageType("PLACE_BETS");
            info.setAnteBet(ante);
            info.setPairPlusBet(pairPlus);
            
            //System.out.println("DEBUG: Sending PLACE_BETS to server");
            //System.out.println("DEBUG: Client is null? " + (client == null));
            
            if (client != null) {
                client.send(info);
                //System.out.println("DEBUG: Message sent successfully");
            } else {
                //System.out.println("ERROR: Client is null!");
                showError("Error", "Client connection is null!");
                return;
            }
            
            dealButton.setDisable(true);
            anteField.setDisable(true);
            pairPlusField.setDisable(true);
            
            showMessage("Bets placed! Dealing cards...");
            
        } catch (NumberFormatException e) {
            //System.out.println("DEBUG: NumberFormatException: " + e.getMessage());
            showMessage("Please enter valid bet amounts");
        } catch (Exception e) {
            //System.out.println("ERROR: Exception in handleDeal: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Failed to send bet: " + e.getMessage());
        }
    }
    
    ///////////////////////
    // HANDLE DEAL CARDS //
    ///////////////////////
    public void handleDealCards(PokerInfo info) {
        System.out.println("DEBUG: handleDealCards() called");
        currentGameInfo = info;
        
        // Display player cards with CSS CLASSES
        ArrayList<Card> playerHand = info.getPlayerHand();
        if (playerHand != null && playerHand.size() == 3) {
            setCardWithCssClass(playerCard1, playerHand.get(0));
            setCardWithCssClass(playerCard2, playerHand.get(1));
            setCardWithCssClass(playerCard3, playerHand.get(2));
            //System.out.println("DEBUG: Player cards displayed with CSS classes");
        } else {
            System.out.println("Player hand is null or wrong size");
        }
        
        // Hide dealer cards with CSS class
        dealerCard1.setText("?");
        dealerCard2.setText("?");
        dealerCard3.setText("?");
        dealerCard1.getStyleClass().clear();
        dealerCard2.getStyleClass().clear();
        dealerCard3.getStyleClass().clear();
        dealerCard1.getStyleClass().addAll("card", "card-hidden");
        dealerCard2.getStyleClass().addAll("card", "card-hidden");
        dealerCard3.getStyleClass().addAll("card", "card-hidden");
        
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
    
    /////////////////
    // HANDLE PLAY //
    /////////////////
    @FXML
    public void handlePlay() {
        //System.out.println("DEBUG: handlePlay() called");
        
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
    
    /////////////////
    // HANDLE FOLD //
    /////////////////
    @FXML
    public void handleFold() {
        //System.out.println("DEBUG: handleFold() called");
        
        currentGameInfo.setMessageType("PLAYER_DECISION");
        currentGameInfo.setPlayerFolded(true);
        
        client.send(currentGameInfo);
        
        playButton.setDisable(true);
        foldButton.setDisable(true);
        
        showMessage("Folded.");
    }
    
	////////////////////////
	// HANDLE GAME RESULT //
	////////////////////////
	public void handleGameResult(PokerInfo info) {
		//System.out.println("DEBUG: handleGameResult() called");
		
		gameInProgress = true;
		
		// Show dealer cards with CSS CLASSES
		ArrayList<Card> dealerHand = info.getDealerHand();
		if (dealerHand != null && dealerHand.size() == 3) {
			setCardWithCssClass(dealerCard1, dealerHand.get(0));
			setCardWithCssClass(dealerCard2, dealerHand.get(1));
			setCardWithCssClass(dealerCard3, dealerHand.get(2));
			System.out.println("DEBUG: Dealer cards revealed with CSS classes");
		}
		
		// Update total winnings
		totalWinnings += info.getTotalWinnings();
		totalWinningsLabel.setText("$" + totalWinnings);
		
		if (info.getMessage() != null) {
			showMessage(info.getMessage());
		}
		
		// Wait 5 seconds so user can see dealer's cards
		new Thread(() -> {
		try {
				Thread.sleep(5000); // 5 seconds delay
				Platform.runLater(() -> {
				if (gameInProgress) {  
				  try {
				      showResultScreen(info);
				      gameInProgress = false; 
				  } catch (Exception e) {
				      e.printStackTrace();
				  }
				}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
    
    //////////////////////////////
    /// SET CARD WITH CSS CLASS //
    //////////////////////////////
    public void setCardWithCssClass(Label label, Card card) {
        String text = card.toShortString();
        label.setText(text);
        
        // Clear existing style classes
        label.getStyleClass().clear();
        
        // Add base card class
        label.getStyleClass().add("card");
        
        // Add color class based on suit
        char suit = card.getSuit();
        if (suit == 'H' || suit == 'D') {
            // Hearts and Diamonds = RED
            label.getStyleClass().add("card-red");
        } else {
            // Clubs and Spades = BLACK
            label.getStyleClass().add("card-black");
        }
        
        //System.out.println("DEBUG: Set card " + text + " with CSS class (suit: " + suit + ")");
    }
    
    /////////////////////////
    // SHOW RESULTS SCREEN //
    /////////////////////////
    public void showResultScreen(PokerInfo info) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/result.fxml"));
        Parent root = loader.load();
        
        ResultController controller = loader.getController();
        controller.setGameResult(info, totalWinnings, client, this);
        
        Stage stage = (Stage) dealButton.getScene().getWindow();
        Scene scene = new Scene(root, 900, 650);
        stage.setScene(scene);
        stage.centerOnScreen();
        
        //System.out.println("DEBUG: Showing result screen (900x650, centered)");
    }
    
	////////////////////////
	// RESET FOR NEW GAME //
	////////////////////////
	public void resetForNewGame() {
		gameInProgress = false;
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
    
    /////////////////
    // HANDLE EXIT //
    /////////////////
    @FXML
    public void handleExit() {
        if (client != null) {
            PokerInfo info = new PokerInfo();
            info.setMessageType("DISCONNECT");
            client.send(info);
        }
        
        Platform.exit();
        System.exit(0);
    }
    
	////////////////////////
	// HANDLE FRESH START //
	////////////////////////
	@FXML
	public void handleFreshStart() {
		gameInProgress = false;
		totalWinnings = 0;
		totalWinningsLabel.setText("$0");
		resetForNewGame();
		showMessage("Fresh start! Total winnings reset to $0");
	}
    
	/////////////////////
	// HANDLE NEW LOOK //
	/////////////////////
	@FXML
	public void handleNewLook() {
		newLookEnabled = !newLookEnabled;
		
		rootPane.getStylesheets().clear();
		
		if (newLookEnabled) {
			rootPane.getStylesheets().add(getClass().getResource("/styles/styles2.css").toExternalForm());
		} else {
			rootPane.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
		}
	}
    
    //////////////////
    // SHOW MESSAGE //
    //////////////////
    public void showMessage(String message) {
        gameInfoArea.appendText(message + "\n");
    }
    
    ////////////////
    // SHOW ERROR //
    ////////////////
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /////////////////////
    // UPDATE DISPLAYS //
    /////////////////////
    public void updateDisplays() {
        totalWinningsLabel.setText("$" + totalWinnings);
    }
    
    /////////////////
    // CLEAR CARDS //
    /////////////////
    public void clearCards() {
        playerCard1.setText("");
        playerCard2.setText("");
        playerCard3.setText("");
        dealerCard1.setText("");
        dealerCard2.setText("");
        dealerCard3.setText("");
        
        // Clear CSS classes and set to empty
        playerCard1.getStyleClass().clear();
        playerCard2.getStyleClass().clear();
        playerCard3.getStyleClass().clear();
        dealerCard1.getStyleClass().clear();
        dealerCard2.getStyleClass().clear();
        dealerCard3.getStyleClass().clear();
        
        playerCard1.getStyleClass().addAll("card", "card-empty");
        playerCard2.getStyleClass().addAll("card", "card-empty");
        playerCard3.getStyleClass().addAll("card", "card-empty");
        dealerCard1.getStyleClass().addAll("card", "card-empty");
        dealerCard2.getStyleClass().addAll("card", "card-empty");
        dealerCard3.getStyleClass().addAll("card", "card-empty");
    }
    
} //EOC