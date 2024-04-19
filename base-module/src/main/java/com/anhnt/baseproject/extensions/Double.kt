package com.anhnt.baseproject.extensions

fun Double?.orZero(): Double {
    return this ?: 0.0
}