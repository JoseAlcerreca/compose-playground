//package com.example.jalc.myapplication.asstate
//
//import org.junit.Test
//
//import androidx.activity.ComponentActivity
//import androidx.compose.foundation.layout.Column
//import androidx.compose.material.Button
//import androidx.compose.material.SnackbarHostState
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.test.junit4.createAndroidComposeRule
//import androidx.compose.ui.test.onNodeWithText
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import kotlinx.coroutines.CoroutineExceptionHandler
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.channels.BufferOverflow
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.flow.consumeAsFlow
//import kotlinx.coroutines.flow.receiveAsFlow
//import kotlinx.coroutines.launch
//import org.junit.Assert.fail
//import org.junit.Rule
//import org.junit.runner.RunWith
//import java.lang.IllegalStateException
//import kotlin.coroutines.CoroutineContext
//import kotlin.coroutines.EmptyCoroutineContext
//import kotlin.test.assertFailsWith
//
//class EffectsAsStateTest {
//
//    @Test
//    fun test1() {
//
//    }
//
//}
//
//
//private class MyViewModel() : ViewModel() {
//    val uiState by mutableStateOf(UiState())
//    init {
//        uiState.snackbarMessage.add("Hello from VM")
//        uiState.snackbarMessage.add("And hello again")
//    }
//
//    fun addMsg() {
//        uiState.snackbarMessage.add("Hello from user")
//    }
//}
//@Composable
//private fun MyComposable(viewModel: MyViewModel, snackbarHostState: SnackbarHostState) {
//    val uiState = viewModel.uiState
//    if  (uiState.snackbarMessage.isNotEmpty()) {
//        LaunchedEffect(uiState.snackbarMessage) {
//            snackbarHostState.showSnackbar(uiState.snackbarMessage)
//
//        }
//    }
//
//    Button(onClick = { viewModel.addMsg() }) {
//        Text("Click me")
//    }
//}
//
//private class UiState {
//    val snackbarMessage = mutableListOf<String>()
//}