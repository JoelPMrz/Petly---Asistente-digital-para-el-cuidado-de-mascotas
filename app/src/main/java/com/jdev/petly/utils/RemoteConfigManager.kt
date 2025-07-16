package com.jdev.petly.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class RemoteConfigManager(context: Context) {

    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private val prefs: SharedPreferences = context.getSharedPreferences("remote_config_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val LAST_FETCH_KEY = "last_fetch_time"
        private const val FETCH_INTERVAL_MILLIS = 60 * 60 * 1000L
    }

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(
            mapOf(
                "latest_version" to "1.0.0",
                "minimum_version" to "1.0.0"
            )
        )
    }

    fun fetchRemoteConfigIfNeeded(onComplete: () -> Unit) {
        val lastFetchTime = prefs.getLong(LAST_FETCH_KEY, 0L)
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastFetchTime > FETCH_INTERVAL_MILLIS) {
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        prefs.edit().putLong(LAST_FETCH_KEY, currentTime).apply()
                    }
                    onComplete()
                }
        } else {
            onComplete()
        }
    }

    fun getLatestVersion(): String {
        return remoteConfig.getString("latest_version")
    }

    fun getMinimumVersion(): String {
        return remoteConfig.getString("minimum_version")
    }
}
