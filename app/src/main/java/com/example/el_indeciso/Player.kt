package com.example.el_indeciso

import java.util.*

data class Player(var playerId: String, var UI: UI_Player?) {

    var cards: Vector<Int> = Vector<Int>() // PRIVATE

    fun addCard(card: Int) {
        cards.add(card)
    }

    fun useCard(card: Int) {
        cards.remove(card)
        //Animacion tiro carta
    }

    @JvmName("getCards1")
    fun getCards(): Vector<Int> {
        return cards
    }

    fun deleteAllCards() {
        cards.removeAllElements()
    }

    fun getCardsAmount(): Int {
        return cards.size
    }

    fun hasCard(card: Int): Boolean {
        return cards.contains(card)
    }

    fun hasLowerCardThan(playedCard: Int): Boolean {
        var hasLowerCard: Boolean = false
        for (card in cards) {
            if (card < playedCard) {
                hasLowerCard = true
            }
        }
        return hasLowerCard
    }

    @JvmName("getUI1")
    fun getUI(): UI_Player? {
        return UI
    }
}