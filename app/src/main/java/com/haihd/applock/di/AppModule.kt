package com.haihd.applock.di

import com.haihd.applock.viewmodel.*
import com.haihd.applock.viewmodel.IconCamouflageViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single { AppLockViewModel(androidApplication()) }
    single { TakePhotoViewModel(androidApplication()) }
    single { HideImageViewModel(androidApplication()) }
    single { HideVideoModel(androidApplication()) }
    single { RestoreImageViewModel(androidApplication()) }
    single { RestoreAudioViewModel(androidApplication()) }
    single { RestoreVideoViewModel(androidApplication()) }
    single { NotifyViewModel(androidApplication()) }
    single { CleanerViewModel(androidApplication()) }
    single { ThemeViewModel(androidApplication()) }
    single { SameImageViewModel(androidApplication()) }
    single { LanguageViewModel(androidApplication()) }
    single { IconCamouflageViewModel(androidApplication()) }
}