package shared; // Include in both server and client projects

import java.io.Serializable;
import java.util.ArrayList;

public class PokerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Message type
    private String messageType; // "PLACE_BETS", "DEAL_CARDS", "PLAYER_DECISION", "GAME_RESULT", "DISCONNECT"
    
    // Betting information
    private int anteBet;
    private int pairPlusBet;
    private int playBet;
    
    // Game state
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;
    private boolean playerFolded;
    
    // Results
    private String gameResult; // "WIN", "LOSE", "PUSH", "FOLD"
    private String pairPlusResult; // "WIN", "LOSE"
    private int totalWinnings;
    private int pairPlusWinnings;
    private boolean dealerQualifies;
    
    // Messages
    private String message;
    
    // Constructors
    public PokerInfo() {
        this.playerHand = new ArrayList<>();
        this.dealerHand = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public int getAnteBet() {
        return anteBet;
    }
    
    public void setAnteBet(int anteBet) {
        this.anteBet = anteBet;
    }
    
    public int getPairPlusBet() {
        return pairPlusBet;
    }
    
    public void setPairPlusBet(int pairPlusBet) {
        this.pairPlusBet = pairPlusBet;
    }
    
    public int getPlayBet() {
        return playBet;
    }
    
    public void setPlayBet(int playBet) {
        this.playBet = playBet;
    }
    
    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }
    
    public void setPlayerHand(ArrayList<Card> playerHand) {
        this.playerHand = playerHand;
    }
    
    public ArrayList<Card> getDealerHand() {
        return dealerHand;
    }
    
    public void setDealerHand(ArrayList<Card> dealerHand) {
        this.dealerHand = dealerHand;
    }
    
    public boolean isPlayerFolded() {
        return playerFolded;
    }
    
    public void setPlayerFolded(boolean playerFolded) {
        this.playerFolded = playerFolded;
    }
    
    public String getGameResult() {
        return gameResult;
    }
    
    public void setGameResult(String gameResult) {
        this.gameResult = gameResult;
    }
    
    public String getPairPlusResult() {
        return pairPlusResult;
    }
    
    public void setPairPlusResult(String pairPlusResult) {
        this.pairPlusResult = pairPlusResult;
    }
    
    public int getTotalWinnings() {
        return totalWinnings;
    }
    
    public void setTotalWinnings(int totalWinnings) {
        this.totalWinnings = totalWinnings;
    }
    
    public int getPairPlusWinnings() {
        return pairPlusWinnings;
    }
    
    public void setPairPlusWinnings(int pairPlusWinnings) {
        this.pairPlusWinnings = pairPlusWinnings;
    }
    
    public boolean isDealerQualifies() {
        return dealerQualifies;
    }
    
    public void setDealerQualifies(boolean dealerQualifies) {
        this.dealerQualifies = dealerQualifies;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}