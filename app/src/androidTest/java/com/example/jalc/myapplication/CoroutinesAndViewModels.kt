package com.example.jalc.myapplication

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineAndViewModels {

    private lateinit var mainDispatcher: CoroutineDispatcher

    // Use ComponentActivity because we want to call setContent and inject a dispatcher into the VM
    // but in a real app, you could use DI.
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testCoroutineDispatcherWithFlow_advanceUntilIdle() {
        val dispatcher = TestCoroutineDispatcher()

        composeTestRule.setContent {
            LoadingOrOk(FlowViewModel(dispatcher))
        }
        dispatcher.advanceUntilIdle()

        composeTestRule.onNodeWithText(NAME1)
            .performClick()
        dispatcher.advanceUntilIdle()

        composeTestRule.onNodeWithText(NAME2)
            .assertExists()
    }
    @Test
    fun testCoroutineDispatcherWithFlow_WithoutContext_advanceUntilIdle() {
        val dispatcher = TestCoroutineDispatcher()

        Dispatchers.setMain(dispatcher) // This is probably doing something bad to Compose
        composeTestRule.setContent {
            LoadingOrOk(FlowViewModel(dispatcher))
        }
        dispatcher.advanceUntilIdle()

        composeTestRule.onNodeWithText(NAME1)
            .assertExists()                     // Timeouts!

        composeTestRule.onNodeWithText(BT_TEXT)
            .performClick()
        dispatcher.advanceUntilIdle()

        composeTestRule.onNodeWithText(NAME2)
            .assertExists()
    }

    @After
    fun reset() {
        Dispatchers.resetMain()
    }

    @Test
    fun testCoroutineDispatcherWithFlow_advanceTime() {
        val dispatcher = TestCoroutineDispatcher()

        composeTestRule.setContent {
            LoadingOrOk(FlowViewModel(dispatcher))
        }
        dispatcher.advanceTimeBy(1500)

        composeTestRule.onNodeWithText(NAME1)
            .performClick()

        composeTestRule.onNodeWithText(NAME2)
            .assertExists()
    }
}

@Composable
fun LoadingOrOk(viewmodel: FlowViewModel) = Column {
    val result: State<Result<String>> = viewmodel.userName.collectAsState(Result.Loading)
    Text(result.value.data ?: "Loading", modifier = Modifier.clickable {
        viewmodel.refreshUserNameWithContext()
    })
    Button(onClick = { viewmodel.refreshUserNameWithoutContext()  }) {
        Text(BT_TEXT)
    }
}

class FlowViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) : ViewModel() {

    private val _userName: MutableStateFlow<Result<String>> = MutableStateFlow(Result.Loading)
    val userName: StateFlow<Result<String>> = _userName

    init {
        viewModelScope.launch {
            withContext(dispatcher) {
                delay(1000)
                _userName.value = Result.Success(NAME1)
            }
        }
    }

    fun refreshUserNameWithContext() {
        viewModelScope.launch {
            withContext(dispatcher) {
                delay(1000)
                _userName.value = Result.Success(NAME2)
            }
        }
    }
    fun refreshUserNameWithoutContext() {
        viewModelScope.launch {
            delay(1000)
            _userName.value = Result.Success(NAME2)
        }
    }
}

const val NAME1 = "Norma Jeane Baker"
const val BT_TEXT = "Refresh without context"
const val NAME2 = "Marilyn Monroe"