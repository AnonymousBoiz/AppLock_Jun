package com.anhnt.baseproject.extensions

fun Int?.orZero(): Int {
    return this ?: 0
}