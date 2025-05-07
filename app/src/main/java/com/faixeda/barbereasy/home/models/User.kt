package com.faixeda.barbereasy.home.models

data class User(
    val email: String = "",
    val barber: Boolean = false,
    val firstName: String = "",
    val lastName: String = ""
)