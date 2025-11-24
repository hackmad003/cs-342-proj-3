package shared;

import java.util.ArrayList;
import java.util.Collections;

//////////
// DECK //
//////////
public class Deck {
    private ArrayList<Card> cards;
    
    //////////
    // DECK //
    //////////
    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
    }
    
    /////////////////////
    // INITIALIZE DECK //
    /////////////////////
    private void initializeDeck() {
        char[] suits = {'C', 'D', 'H', 'S'};
        
        for (char suit : suits) {
            for (int value = 2; value <= 14; value++) {
                cards.add(new Card(suit, value));
            }
        }
    }
    
    /////////////
    // SHUFFLE //
    /////////////
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    ///////////////
    // DEAL CARD //
    ///////////////
    public Card dealCard() {
        if (cards.isEmpty()) {
            // Reset deck if empty
            initializeDeck();
            shuffle();
        }
        return cards.remove(0);
    }
    
    /////////////////////
    // CARDS REMAINIGN //
    /////////////////////
    public int cardsRemaining() {
        return cards.size();
    }
    
    ///////////
    // RESET //
    ///////////
    public void reset() {
        cards.clear();
        initializeDeck();
    }
    
} //EOC