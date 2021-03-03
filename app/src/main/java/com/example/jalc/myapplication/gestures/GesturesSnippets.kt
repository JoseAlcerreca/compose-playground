package com.example.jalc.myapplication.gestures

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * This file lets DevRel track changes to snippets present in
 * https://developer.android.com/jetpack/compose/gestures
 *
 * No action required if it's modified.
 */

private object GesturesSnippet1 {
    @Composable
    fun ClickableSample() {
        val count = remember { mutableStateOf(0) }
        // content that you want to make clickable
        Text(
            text = count.value.toString(),
            modifier = Modifier.clickable { count.value += 1 }.padding(24.dp)
        )

    }
}

@Preview @Composable fun Preview1() {
    Surface {
        Box(
            modifier = Modifier.background(Color.LightGray).size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            GesturesSnippet1.ClickableSample()
        }
    }
}
