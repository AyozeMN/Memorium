package com.ayoze.memorium.model

data class Patient(
    val email: String ?= "",
    val name: String ?= "",
    val lastName: String ?= "",
    val birthDate: String ?= "",
    val profilePicture: String ?= ""
)