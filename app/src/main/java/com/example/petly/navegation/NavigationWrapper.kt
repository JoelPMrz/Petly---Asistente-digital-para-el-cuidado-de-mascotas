package com.example.petly.navegation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.petly.ui.screens.auth.ForgotPasswordScreen
import com.example.petly.ui.screens.auth.LoginScreen
import com.example.petly.ui.screens.auth.SingUpScreen
import com.example.petly.ui.screens.login.UserDetailScreen
import com.example.petly.ui.screens.login.home.HomeScreen
import com.example.petly.ui.screens.login.pet.CreatePetScreen
import com.example.petly.ui.screens.login.pet.PetDetailScreen
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import com.example.petly.utils.RealtimeManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User

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
                navigateToHome ={
                    navController.navigate(Home){
                        popUpTo<Login>{
                            inclusive = true
                        }
                    }
                },
                navigateToForgotPassword = {
                    navController.navigate(ForgotPassword)
                },
                navigateToSingUp = {
                    navController.navigate(SingUp)
                }
            )
        }
        composable<ForgotPassword> {
            ForgotPasswordScreen(
                analytics = analytics ,
                auth = authManager,
                navigateToLogin = {
                    navController.navigate(Login)
                }
            )
        }
        composable<SingUp> {
            SingUpScreen(
                analytics = analytics ,
                auth = authManager,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<Home> {
            HomeScreen(
                analytics = analytics ,
                auth = authManager,
                navigateToPetDetail = { petId ->
                    navController.navigate(PetDetail(petId = petId))
                },
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToCreatePet = {
                    navController.navigate(CreatePet)
                }
            )
        }
        composable<UserDetail> {
            UserDetailScreen(
                analytics = analytics ,
                auth =  authManager,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<CreatePet> {
            CreatePetScreen(
                analytics = analytics,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<PetDetail> { backStackEntry->
            val petDetail: PetDetail = backStackEntry.toRoute()
            PetDetailScreen(
                analytics = analytics ,
                petId = petDetail.petId,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
