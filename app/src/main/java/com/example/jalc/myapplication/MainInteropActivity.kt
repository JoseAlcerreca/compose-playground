package com.example.jalc.myapplication

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import com.example.jalc.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.get

class MainInteropActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    AndroidViewSample()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun InteropPreview() {
    MyApplicationTheme {
        AndroidViewSample()
    }
}

@Composable
fun AndroidViewSample() {

    val text = remember { mutableStateOf("Hello Views") }
    Column {
        Button(onClick = {text.value = "Hello Compose" } ) {
            Text("Click here")
        }
        AndroidView(
            modifier = Modifier.fillMaxHeight(),
            update = { (it[0] as TextView).text = text.value },
            viewBlock = {
                FrameLayout(it).apply {
                    setPadding(100, 100, 100, 100)
                    setBackgroundColor(0xFF888888.toInt())
                    layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
                    )
                    val tv = TextView(it)
                    tv.text = text.value
                    addView(tv)
                }
            }
        )
    }
}