package com.example.jalc.myapplication.events

import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

private class MyViewModel : ViewModel() {

    private val _snackbarMessages = mutableStateListOf<String>()
    val snackbarMessages: List<String> = _snackbarMessages

    init {
        _snackbarMessages.add("Hello from VM")
        _snackbarMessages.add("And hello again")
    }

    fun addMsg() {
        _snackbarMessages.add("Hello from user")
    }

    fun resetSnackbar() {
        _snackbarMessages.clear()
    }
}

private class MyViewModelFlow : ViewModel() {

    private val _snackbarMessages = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val snackbarMessages: Flow<String> = _snackbarMessages

    init {
        viewModelScope.launch {
            _snackbarMessages.emit("Hello from VM")
            _snackbarMessages.emit("And hello again")
        }
    }

    fun addMsg() {
        viewModelScope.launch {
            _snackbarMessages.emit("Hello from user")
        }
    }
}

@Composable
private fun MyComposable(snackbarHostState: SnackbarHostState) {

    val viewModel: MyViewModel = viewModel()
    val snackbarMessages = viewModel.snackbarMessages
    if  (snackbarMessages.isNotEmpty()) {
        LaunchedEffect(snackbarMessages) {
            snackbarMessages.forEach { msg ->
                snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            }
            viewModel.resetSnackbar() // FIXME: concurrent modifications?
        }
    }

    Button(onClick = { viewModel.addMsg() }) {
        Text("Click me")
    }
}

@Preview
@Composable
fun MyComposablePreview() {
    val scaffoldState = rememberScaffoldState()

    Scaffold(scaffoldState = scaffoldState) {
        MyComposable(
            snackbarHostState = scaffoldState.snackbarHostState
        )

    }
}

private class UiState {
}