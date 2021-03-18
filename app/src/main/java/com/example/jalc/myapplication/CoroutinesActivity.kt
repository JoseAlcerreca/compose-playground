package com.example.jalc.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CoroutinesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var timer by remember { mutableStateOf(0) }
            LaunchedEffect(Unit) {
                delay(1000)
                timer += 1
            }
            Column {
                LoadingDataThing()
                if (timer > 0) {
                    LoadingDataThing()
                }
            }
        }
    }
}


@OptIn(InternalCoroutinesApi::class)
@Composable
fun LoadingDataThing(
    viewmodel: MyViewModel = viewModel(factory = MyFactory()),
    navigateBack: () -> Unit = {}
) = Column {
    LaunchedEffect(key1 = viewmodel.effects) {
        viewmodel.effects.collect {

            when(it) {
                is MyViewModelEffects.navigateBack -> {
                    Log.d("jalc", "navigateBack")
                    navigateBack()
                }
                is MyViewModelEffects.navigateToDestination2 -> {
                    Log.d("jalc", "navigateToDestination2: ${it.id}")
                    //navController.navigate("details", it.id)
                }
                else -> {}
            }
        }
    }
    val result = produceState<Result<String>>(Result.Loading) { value = viewmodel.giveMeData() }
    Text(result.value.data ?: "Loading")
}

class MyViewModel(private val dispatcher: CoroutineDispatcher) : ViewModel() {
    // Allow multiple collectors
    private val _effects = MutableSharedFlow<MyViewModelEffects>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
//  private val _effects = Channel<MyViewModelEffects>(Channel.BUFFERED)
    val effects: Flow<MyViewModelEffects> = _effects

    suspend fun giveMeData(): Result<String> {
        return withContext(dispatcher) {
            delay(500)
            Result.Success("OK!")
        }
    }

    init {
        viewModelScope.launch {
            var count = 1
            while(isActive) {
                delay(1501)
                count += 1
                _effects.emit(MyViewModelEffects.navigateToDestination2(count.toString()))
            }
        }
    }

}

class MyFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MyViewModel(Dispatchers.Main) as T
    }
}

sealed class MyViewModelEffects {
    object navigateBack : MyViewModelEffects()
    class navigateToDestination1: MyViewModelEffects()
    class navigateToDestination2(val id: String): MyViewModelEffects()
}