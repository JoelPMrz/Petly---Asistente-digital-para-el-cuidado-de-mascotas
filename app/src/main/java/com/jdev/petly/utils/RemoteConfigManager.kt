package com.jdev.petly.utils

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class RemoteConfigManager {

    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

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

    fun fetchRemoteConfig(onComplete: () -> Unit) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete()
                } else {
                    onComplete()
                }
            }
    }

    fun getLatestVersion(): String {
        return remoteConfig.getString("latest_version")
    }

    fun getMinimumVersion(): String {
        return remoteConfig.getString("minimum_version")
    }
}

