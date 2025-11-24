package shared;

import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private char suit; // 'C', 'D', 'H', 'S'
    private int value; // 2-14 (11=Jack, 12=Queen, 13=King, 14=Ace)
    
    public Card(char suit, int value) {
        this.suit = suit;
        this.value = value;
    }
    
    public char getSuit() {
        return suit;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getSuitName() {
        switch(suit) {
            case 'C': return "Clubs";
            case 'D': return "Diamonds";
            case 'H': return "Hearts";
            case 'S': return "Spades";
            default: return "Unknown";
        }
    }
    
    public String getValueName() {
        switch(value) {
            case 11: return "Jack";
            case 12: return "Queen";
            case 13: return "King";
            case 14: return "Ace";
            default: return String.valueOf(value);
        }
    }
    
    @Override
    public String toString() {
        return getValueName() + " of " + getSuitName();
    }
    
    // Short notation for display - using text instead of Unicode symbols
    public String toShortString() {
        String valueStr;
        switch(value) {
            case 11: valueStr = "J"; break;
            case 12: valueStr = "Q"; break;
            case 13: valueStr = "K"; break;
            case 14: valueStr = "A"; break;
            default: valueStr = String.valueOf(value);
        }
        
        // Use text abbreviations instead of Unicode symbols
        String suitSymbol;
        switch(suit) {
            case 'C': suitSymbol = "C"; break;  // Clubs
            case 'D': suitSymbol = "D"; break;  // Diamonds
            case 'H': suitSymbol = "H"; break;  // Hearts
            case 'S': suitSymbol = "S"; break;  // Spades
            default: suitSymbol = "?";
        }
        
        return valueStr + suitSymbol;
    }
}