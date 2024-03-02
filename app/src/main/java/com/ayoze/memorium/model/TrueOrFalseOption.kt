package com.ayoze.memorium.model

class TrueOrFalseOption(
    override val optionStatement: String? = "",
    private val isCorrect: Boolean? = false
) : Option() {
    override fun isCorrect(): Boolean? {
        return isCorrect
    }
}