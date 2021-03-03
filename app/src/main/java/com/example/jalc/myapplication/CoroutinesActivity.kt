package com.example.jalc.myapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


class CoroutinesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoadingDataThing()
        }
    }
}

@Composable
fun LoadingDataThing(viewmodel: MyViewModel = viewModel(factory = MyFactory())) = Column {
    val result = produceState<Result<String>>(Result.Loading) { value = viewmodel.giveMeData() }
    Text(result.value.data ?: "Loading")
}

class MyViewModel(private val dispatcher: CoroutineDispatcher) : ViewModel() {
    suspend fun giveMeData(): Result<String> {
        return withContext(dispatcher) {
            delay(500)
            Result.Success("OK!")
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MyFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MyViewModel(Dispatchers.Main) as T
    }

}