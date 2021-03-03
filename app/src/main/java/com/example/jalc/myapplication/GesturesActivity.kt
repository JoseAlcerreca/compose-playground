package com.example.jalc.myapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.jalc.myapplication.ui.theme.MyApplicationTheme


class GesturesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                ) {
//                    var page by remember { mutableStateOf("") }
//                    val pages = linkedMapOf<String, @Composable () -> Unit>(
//                        "ZoomableSample" to { ZoomableSample() },
//                        "DetectMultitouchGestures" to { DetectMultitouchGestures() }

//                    )
//                    if (page == "") {
//                        Column {
//                            pages.forEach {
//                                Button(onClick = { page = it.key }) { Text(it.key) }
//                            }
//                        }
//                    } else {
//                        pages[page]?.invoke() ?: Text("error")
//                    }
                    //ZoomableSample()
                   //DetectMultitouchGestures()
                    //SwipeableSample()
//                    DragSnippet2()
//                    ScrollBoxes()
//                    ScrollBoxesSmooth()
                    //NestedScrollSample()
                    //ScrollableSample()
//                    Nested2()
                    //BigBox()
                    //ClickableSample()
//                    Column {
//                        Greeting()
//                        DraggableDemo()
//                        DetectDragGesturesSample()
//                        ZoomableDemo()
//                    }
                }
            }
        }
    }
}


