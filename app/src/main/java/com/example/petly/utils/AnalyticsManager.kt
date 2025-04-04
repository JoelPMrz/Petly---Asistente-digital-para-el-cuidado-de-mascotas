package com.example.petly.utils

import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.analytics.ktx.analytics

class AnalyticsManager(context: Context) {

    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }

    private fun logEvent(eventName: String, params: Bundle){
        firebaseAnalytics.logEvent(eventName, params)
    }

    fun logButtonClicked(buttonName: String){
        val params = Bundle().apply {
            putString("button_name", buttonName)
        }
        logEvent("button_clicked", params)
    }

    fun logError(error: String){
        val params = Bundle().apply {
            putString("error", error)
        }
        logEvent("error", params)
    }

    fun logButtonEvent(buttonName: String){
        val params = Bundle().apply {
            putString("button_name", buttonName)
        }
        logEvent("button_clicked", params)
    }

    @Composable
    fun LogScreenView(screenName: String){
        DisposableEffect(Unit) {
            onDispose {
                val params = Bundle().apply {
                    putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                    putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
                }
                logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
            }
        }
    }
}
