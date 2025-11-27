package com.beavuck.stop_and_go.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.beavuck.stop_and_go.repositories.ConfigRepository
import java.util.Locale

abstract class LocalizedActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(applyLocale(newBase))
    }

    private fun applyLocale(context: Context): Context {
        val configRepository = ConfigRepository(context)
        val savedLocale = configRepository.loadLocale()

        if (savedLocale.isNullOrEmpty()) {
            return context
        }

        val locale = Locale.forLanguageTag(savedLocale)
        val config = context.resources.configuration
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
