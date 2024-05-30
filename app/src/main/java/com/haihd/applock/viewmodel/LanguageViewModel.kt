package com.haihd.applock.viewmodel

import android.app.Application
import com.haihd.applock.R
import com.haihd.applock.item.ItemLanguage
import com.anhnt.baseproject.viewmodel.BaseViewModel

class LanguageViewModel(application: Application) : BaseViewModel(application) {
    val languageList = arrayListOf(
        ItemLanguage(R.drawable.flag_en, "English", "en"),
        ItemLanguage(R.drawable.flag_ar, "Arabic", "ar"),
        ItemLanguage(R.drawable.flag_cs, "Czech", "cs"),
        ItemLanguage(R.drawable.flag_de, "German", "de"),
        ItemLanguage(R.drawable.flag_el, "Greek", "el"),
        ItemLanguage(R.drawable.flag_es, "Spanish", "es"),
        ItemLanguage(R.drawable.flag_fr, "French", "fr"),
        ItemLanguage(R.drawable.flag_hi, "Hindi", "hi"),
        ItemLanguage(R.drawable.flag_in, "Indonesian", "in"),
        ItemLanguage(R.drawable.flag_it, "Italian", "it"),
        ItemLanguage(R.drawable.flag_ja, "Japan", "ja"),
        ItemLanguage(R.drawable.flag_ko, "Korean", "ko"),
        ItemLanguage(R.drawable.flag_nl, "Dutch", "nl"),
        ItemLanguage(R.drawable.flag_bg, "Bulgarian", "bg"),
        ItemLanguage(R.drawable.flag_pl, "Polish", "pl"),
        ItemLanguage(R.drawable.flag_pt, "Portuguese", "pt"),
        ItemLanguage(R.drawable.flag_ro, "Romanian", "ro"),
        ItemLanguage(R.drawable.flag_ru, "Russian", "ru"),
        ItemLanguage(R.drawable.flag_sv, "Swedish", "sv"),
        ItemLanguage(R.drawable.flag_th, "Thai", "th"),
        ItemLanguage(R.drawable.flag_vi, "Vietnamese", "vi"),
        ItemLanguage(R.drawable.flag_zh, "Chinese", "zh")
    )
}