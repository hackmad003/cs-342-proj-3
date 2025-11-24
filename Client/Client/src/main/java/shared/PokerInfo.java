package shared; 

import java.io.Serializable;
import java.util.ArrayList;


//////////////////////
// POKER INFO CLASS //
//////////////////////
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
    
    ////////////////
    // POKER INFO //
    ////////////////
    public PokerInfo() {
        this.playerHand = new ArrayList<>();
        this.dealerHand = new ArrayList<>();
    }
    
    //////////////////////
    // GET MESSAGE TYPE //
    //////////////////////
    public String getMessageType() {
        return messageType;
    }
    
    //////////////////////
    // SET MESSAGE TYPE //
    //////////////////////
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    //////////////////
    // GET ANTE BET //
    //////////////////
    public int getAnteBet() {
        return anteBet;
    }
    
    //////////////////
    // SET ANTE BET //
    //////////////////
    public void setAnteBet(int anteBet) {
        this.anteBet = anteBet;
    }
    
    ///////////////////////
    // GET PAIR PLUS BET //
    ///////////////////////
    public int getPairPlusBet() {
        return pairPlusBet;
    }
    
    ///////////////////////
    // SET PAIR PLUS BET //
    ///////////////////////
    public void setPairPlusBet(int pairPlusBet) {
        this.pairPlusBet = pairPlusBet;
    }
    
    //////////////////
    // GET PLAY BET //
    //////////////////
    public int getPlayBet() {
        return playBet;
    }
    
    //////////////////
    // SET PLAY BET //
    //////////////////
    public void setPlayBet(int playBet) {
        this.playBet = playBet;
    }
    
    /////////////////////
    // GET PLAYER HAND //
    /////////////////////
    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }
    
    /////////////////////
    // SET PLAYER HAND //
    /////////////////////
    public void setPlayerHand(ArrayList<Card> playerHand) {
        this.playerHand = playerHand;
    }
    
    /////////////////////
    // GET DEALER HAND //
    /////////////////////
    public ArrayList<Card> getDealerHand() {
        return dealerHand;
    }
    
    /////////////////////
    // SET DEALER HAND //
    /////////////////////
    public void setDealerHand(ArrayList<Card> dealerHand) {
        this.dealerHand = dealerHand;
    }
    
    //////////////////////
    // IS PLAYER FOLDED //
    //////////////////////
    public boolean isPlayerFolded() {
        return playerFolded;
    }
    
    ///////////////////////
    // SET PLAYER FOLDED //
    ///////////////////////
    public void setPlayerFolded(boolean playerFolded) {
        this.playerFolded = playerFolded;
    }
    
    /////////////////////
    // GET GAME RESULT //
    /////////////////////
    public String getGameResult() {
        return gameResult;
    }
    
    /////////////////////
    // SET GAME RESULT //
    /////////////////////
    public void setGameResult(String gameResult) {
        this.gameResult = gameResult;
    }
    
    //////////////////////////
    // GET PAIR PLUS RESULT //
    //////////////////////////
    public String getPairPlusResult() {
        return pairPlusResult;
    }
    
    //////////////////////////
    // SET PAIR PLUS RESULT //
    //////////////////////////
    public void setPairPlusResult(String pairPlusResult) {
        this.pairPlusResult = pairPlusResult;
    }
    
    ///////////////////////
    // GET TOTAL WINNIGS //
    ///////////////////////
    public int getTotalWinnings() {
        return totalWinnings;
    }
    
    ////////////////////////
    // SET TOTAL WINNIGNS //
    ////////////////////////
    public void setTotalWinnings(int totalWinnings) {
        this.totalWinnings = totalWinnings;
    }
    
    ////////////////////////////
    // GET PAIR PLUS WINNIGNS //
    ////////////////////////////
    public int getPairPlusWinnings() {
        return pairPlusWinnings;
    }
    
    ////////////////////////////
    // SET PAIR PLUS WINNIGNS //
    ////////////////////////////
    public void setPairPlusWinnings(int pairPlusWinnings) {
        this.pairPlusWinnings = pairPlusWinnings;
    }
    
    /////////////////////////
    // IS DEALER QUALIFIES //
    /////////////////////////
    public boolean isDealerQualifies() {
        return dealerQualifies;
    }
    
    //////////////////////////
    // SET DEALER QUALIFIES //
    //////////////////////////
    public void setDealerQualifies(boolean dealerQualifies) {
        this.dealerQualifies = dealerQualifies;
    }
    
    /////////////////
    // GET MESSAGE //
    /////////////////
    public String getMessage() {
        return message;
    }
    
    /////////////////
    // SET MESSAGE //
    /////////////////
    public void setMessage(String message) {
        this.message = message;
    }
}