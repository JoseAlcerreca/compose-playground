package com.example.jalc.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay


class CoroutinesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoadingDataThing()
        }
    }
}

@Composable
fun LoadingDataThing(viewmodel: MyViewModel = viewModel()) {
    val result = produceState<Result>(Result.Loading) { value = viewmodel.giveMeData() }
    Text((result.value as? Result.Success)?.data ?: "Loading")
}

class MyViewModel(val dispatcher: CoroutineDispatcher) : ViewModel() {
    suspend fun giveMeData(): Result {
        delay(500)
        return Result.Success("OK!")
    }
}

sealed class Result {
    object Loading : Result()
    data class Success(val data: String) : Result()
}
