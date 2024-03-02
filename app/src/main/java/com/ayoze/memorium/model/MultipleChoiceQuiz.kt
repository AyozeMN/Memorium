package com.ayoze.memorium.model

data class MultipleChoiceQuiz (
    var id: String ?= null,
    var title: String ?= "Sin título",
    var image: String ?= "drawable/memorium_graybg_m.png",
    var difficulty: String ?= null,
    var questions: List<MultipleChoiceQuestion> ?= null
)