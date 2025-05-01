package com.example.petly.navegation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.petly.ui.components.MyNavigationAppBar
import com.example.petly.ui.screens.auth.ForgotPasswordScreen
import com.example.petly.ui.screens.auth.LoginScreen
import com.example.petly.ui.screens.auth.SingUpScreen
import com.example.petly.ui.screens.logged.UserDetailScreen
import com.example.petly.ui.screens.logged.HomeScreen
import com.example.petly.ui.screens.logged.calendar.CalendarScreen
import com.example.petly.ui.screens.logged.pet.AddPetScreen
import com.example.petly.ui.screens.logged.pet.PetDetailScreen
import com.example.petly.ui.screens.logged.user.UserScreen
import com.example.petly.ui.screens.logged.weight.WeightsScreen
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import com.example.petly.utils.CloudStorageManager
import com.google.firebase.auth.FirebaseUser

@Composable
fun NavigationWrapper(context : Context){
    val navController = rememberNavController()
    val storage = CloudStorageManager()
    val analytics = AnalyticsManager(context)
    val authManager = AuthManager(context)
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
                //analytics = analytics ,
                navigateToPetDetail = { petId ->
                    navController.navigate(PetDetail(petId = petId))
                },
                navigateToAddPet = {
                    navController.navigate(AddPet)
                },
                navigateToHome = {
                    navController.navigate(Home){
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateToCalendar = {
                    navController.navigate(Calendar){
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateToUser = {
                    navController.navigate(User){
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable<Calendar>{
            CalendarScreen(
                navigateToHome = {
                    navController.navigate(Home){
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateToCalendar = {
                    navController.navigate(Calendar){
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateToUser = {
                    navController.navigate(User){
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable<User>{
            UserScreen(
                auth = authManager,
                navigateToHome = {
                    navController.navigate(Home){
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateToCalendar = {
                    navController.navigate(Calendar){
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateToUser = {
                    navController.navigate(User){
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateBack = {
                    navController.navigate(Login){
                        popUpTo<Home>{
                            inclusive = true
                        }
                    }
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
        composable<AddPet> {
            AddPetScreen(
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
                },
                navigateToWeights = {
                    navController.navigate(Weights(petDetail.petId))
                },
                navigateToHome = {
                    navController.navigate(Home){
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable<Weights> {backStackEntry->
            val petDetail: Weights = backStackEntry.toRoute()
            WeightsScreen(
                //analytics = analytics,
                petId = petDetail.petId,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
