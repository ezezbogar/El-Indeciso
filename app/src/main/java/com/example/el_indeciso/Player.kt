package com.example.el_indeciso

import java.util.*

data class Player(var playerName: String) {

    private var cards: Vector<Int> = Vector<Int>()

    fun addCard(card: Int) {
        cards.add(card)
    }

    fun useCard(card: Int) {
        cards.remove(card)
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
}