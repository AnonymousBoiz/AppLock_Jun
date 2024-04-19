package com.anhnt.baseproject.extensions

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}