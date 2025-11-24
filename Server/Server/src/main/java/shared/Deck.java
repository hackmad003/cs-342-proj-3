package shared;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;
    
    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
    }
    
    private void initializeDeck() {
        char[] suits = {'C', 'D', 'H', 'S'};
        
        for (char suit : suits) {
            for (int value = 2; value <= 14; value++) {
                cards.add(new Card(suit, value));
            }
        }
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    public Card dealCard() {
        if (cards.isEmpty()) {
            // Reset deck if empty
            initializeDeck();
            shuffle();
        }
        return cards.remove(0);
    }
    
    public int cardsRemaining() {
        return cards.size();
    }
    
    public void reset() {
        cards.clear();
        initializeDeck();
    }
}