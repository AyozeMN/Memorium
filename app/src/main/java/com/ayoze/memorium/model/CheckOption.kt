package com.ayoze.memorium.model

data class CheckOption(
    override val optionStatement: String? = "",
    private val isCorrect: Boolean? = false
) : Option() {
    override fun isCorrect(): Boolean? {
        return isCorrect
    }
}