package com.ayoze.memorium.model

abstract class Option {
    abstract val optionStatement: String?
    abstract fun isCorrect(): Boolean?
}