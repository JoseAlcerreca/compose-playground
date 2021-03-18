package com.example.jalc.myapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class SnackbarsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var screen by remember { mutableStateOf(SnackbarsScreens.FIRST) }
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                delay(1500)
                snackbarHostState.showSnackbar("Hi!")
            }
            Crossfade(targetState = screen, animationSpec = tween(5000)) {
                when (it) {
                    SnackbarsScreens.FIRST -> {
                        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
                            Button(onClick = { screen = SnackbarsScreens.SECOND }) {
                                Text("First!")
                            }
                        }
                    }
                    SnackbarsScreens.SECOND -> {
                        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
                            Button(onClick = { screen = SnackbarsScreens.FIRST }) {
                                Text("Second!")
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class SnackbarsScreens {
    FIRST,
    SECOND
}