package com.ayoze.memorium.model

data class MultipleChoiceQuestion (
    var id: String = "",
    var statement: String ?= "",
    var options: List<CheckOption> ?= null,
    var isSelected: Boolean ?= false
)