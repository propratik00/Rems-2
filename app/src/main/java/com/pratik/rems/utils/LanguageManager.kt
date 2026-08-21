package com.pratik.rems.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LanguageManager {
    /**
     * This function tells the Android system to change the app's language.
     * "en" for English, "hi" for Hindi, "mr" for Marathi.
     */
    fun setLanguage(languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    /**
     * This checks what language is currently active.
     */
    fun getSelectedLanguage(): String {
        return AppCompatDelegate.getApplicationLocales().toLanguageTags() ?: "en"
    }
}