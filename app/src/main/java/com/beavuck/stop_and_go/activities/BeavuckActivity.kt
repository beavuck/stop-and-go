package com.beavuck.stop_and_go.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.utils.instrumented.DebugUtils.maybeSetStrictMode
import java.util.Locale

abstract class BeavuckActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        enableEdgeToEdge()
        maybeSetStrictMode()
        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(applyLocale(newBase))
    }

    @SuppressLint("AppBundleLocaleChanges") // language split is disabled in the bundle config
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
