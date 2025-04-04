package com.example.petly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.petly.navegation.NavigationWrapper
import com.example.petly.ui.theme.PetlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PetlyTheme {
                NavigationWrapper(context = this)
            }
        }
    }
}

