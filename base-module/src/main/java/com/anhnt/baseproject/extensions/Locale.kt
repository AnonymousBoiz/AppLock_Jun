package com.anhnt.baseproject.extensions

import java.util.*

val Locale?.isRussian: Boolean
    get() = this?.language == "ru"