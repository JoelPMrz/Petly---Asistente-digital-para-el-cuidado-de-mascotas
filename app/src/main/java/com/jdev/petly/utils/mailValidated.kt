package com.jdev.petly.utils

import android.util.Patterns

fun mailValidated(mail: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(mail).matches()
}

fun passwordValidated(password: String): Boolean {
    val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")
    return regex.matches(password)
}
