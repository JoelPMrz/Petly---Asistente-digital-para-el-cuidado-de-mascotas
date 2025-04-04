package com.example.petly.navegation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.petly.ui.screens.auth.ForgotPasswordScreen
import com.example.petly.ui.screens.auth.LoginScreen
import com.example.petly.ui.screens.auth.SingUpScreen
import com.example.petly.ui.screens.login.home.HomeScreen
import com.example.petly.ui.screens.login.pet.CreatePetScreen
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import com.example.petly.utils.RealtimeManager
import com.google.firebase.auth.FirebaseUser

@Composable
fun NavigationWrapper(context : Context){
    val navController = rememberNavController()
    val analytics = AnalyticsManager(context)
    val authManager = AuthManager(context)
    val realtime = RealtimeManager(context)
    val user: FirebaseUser? = authManager.getCurrentUser()

    NavHost(navController = navController, startDestination = if(user == null)Login else Home){
        composable<Login> {
            LoginScreen(
                analytics = analytics ,
                auth = authManager,
                navigation = navController
            )
        }
        composable<ForgotPassword> {
            ForgotPasswordScreen(
                analytics = analytics ,
                auth = authManager,
                navigation = navController
            )
        }
        composable<SingUp> {
            SingUpScreen(
                analytics = analytics ,
                auth = authManager,
                navigation = navController
            )
        }
        composable<Home> {
            HomeScreen(
                analytics = analytics ,
                auth = authManager,
                realtime = realtime,
                navigation = navController
            )
        }
        composable<CreatePet> {
            CreatePetScreen(
                analytics = analytics ,
                auth = authManager,
                realtime = realtime,
                navigation = navController
            )
        }
    }
}
