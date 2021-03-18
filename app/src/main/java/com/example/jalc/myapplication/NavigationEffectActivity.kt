package com.example.jalc.myapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class NavigationEffectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewmodel: NavigationEffectVieWModel = viewModel()
            LaunchedEffect(viewmodel) {
                viewmodel.navigationEffects.collect { dest ->
                    navController.navigate(dest)
                }
            }

            NavHost(navController, startDestination = "first") {
                composable("first") {
                        Button(onClick = { navController.navigate("second") }) {
                            Text("First!")
                        }
                }
                composable("second") {
                    Button(onClick = { navController.navigate("first") } ){
                        Text("Second!")
                    }
                }
                composable("auth") {
                    Button(onClick = { navController.navigate("first") } ){
                        Text("Re-auth from server!")
                    }
                }
            }
        }
    }
}

class NavigationEffectVieWModel: ViewModel() {

    // A Conflated channel stores, at most, one element and drops the oldest if there's overflow.
    private val _effects = Channel<String>(CONFLATED)

    // Allow multiple collectors but only one receives data
    val navigationEffects: Flow<String> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            // Simulate an effect coming from a remote server
            delay(2000)
            _effects.send("auth")
        }
    }
}