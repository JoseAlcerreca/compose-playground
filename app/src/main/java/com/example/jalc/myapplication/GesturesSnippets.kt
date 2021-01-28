package com.example.jalc.myapplication


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectMultitouchGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberScrollableController
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.zoomable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.gesture.ScrollCallback
import androidx.compose.ui.gesture.nestedscroll.NestedScrollConnection
import androidx.compose.ui.gesture.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.gesture.nestedscroll.NestedScrollSource
import androidx.compose.ui.gesture.nestedscroll.nestedScroll
import androidx.compose.ui.gesture.scrollGestureFilter
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.jalc.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
private fun BigBox() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var debug by remember { mutableStateOf("None") }
    Box(
        Modifier
            .fillMaxSize()
            .wrapContentSize()) {

        val sizeModifier = Modifier.size(100.dp)
        Box(
            modifier = sizeModifier
//                    .zIndex(10f)
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .background(Color.LightGray)
                .clipToBounds()
                .verticalScroll(rememberScrollState())
//                    .pointerInput {
//                        detectDragGestures { change: PointerInputChange, dragAmount ->
//                            val limitedX = (offsetX + dragAmount.x).coerceIn(0f, 120f)
//                            val limitedY = (offsetY + dragAmount.y).coerceIn(0f, 120f)
//                            change.consumePositionChange(limitedX, limitedY)
//                            offsetX = limitedX
//                            offsetY = limitedY
//                            debug = "Big: $limitedX"
//                        }
//                    }
//                    .draggable(
//                        orientation = Orientation.Vertical,
//                        onDrag = {
//                            debug = "Dragged light"
//                            offsetY += it
//                        })
        ) {
            SmallBox { debug = it }
        }
        Text(debug)

    }
}

@Composable
private fun BoxScope.SmallBox(debug: (String) -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(50.dp)
            .height(60.dp)
//                .zIndex(5f)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .background(Color.DarkGray)
            .align(Alignment.Center)
            .clickable(onClick = { debug("Clicked dark") })
            .pointerInput {
                detectDragGestures { change: PointerInputChange, dragAmount ->
                    val limitedX = (offsetX + dragAmount.x).coerceIn(0f, 20f)
                    val limitedY = (offsetY + dragAmount.y).coerceIn(0f, 20f)
                    change.consumePositionChange(limitedX, limitedY)
                    offsetX = limitedX
                    offsetY = limitedY
                    debug("Small: $limitedX")
                }
            }
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun SwipeableSample() {
    val width = 96.dp
    val squareSize = 48.dp

    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(AmbientDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1)

    Box(
        modifier = Modifier
            .preferredWidth(width)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .background(Color.LightGray)
    ) {
        Box(
            Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .preferredSize(squareSize)
                .background(Color.DarkGray)
        )
    }
}

@Composable
fun Greeting() {
    var tapAction by remember { mutableStateOf("None") }
    var dragAction by remember { mutableStateOf("None") }
    Text(
        modifier = Modifier.pointerInput {
            detectDragGestures { change: PointerInputChange, dragAmount: Offset ->
                dragAction = "Dragged (${dragAmount.x}, ${dragAmount.y}"
            }
            detectTapGestures(
                onDoubleTap = { tapAction = "Double Tap" },
                onLongPress = { tapAction = "Long press" },
                onTap = { tapAction = "Tap" },
                onPress = { tapAction = "Press" }
            )
        },
        text = "Detected: $tapAction - $dragAction!"
    )
}
@Composable
fun DetectDragGesturesSample() {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }
    Box(
        Modifier
            .fillMaxSize()
            .onSizeChanged { size = it.toSize() }
    ) {
        Box(
            Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .size(50.dp)
                .background(Color.Blue)
                .pointerInput {
                    detectDragGestures { change, dragAmount ->
                        val original = Offset(offsetX.value, offsetY.value)
                        val summed = original + dragAmount
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - 50.dp.toPx()),
                            y = summed.y.coerceIn(0f, size.height - 50.dp.toPx())
                        )
                        change.consumePositionChange(
                            consumedDx = newValue.x - original.x,
                            consumedDy = newValue.y - original.y
                        )
                        offsetX.value = newValue.x
                        offsetY.value = newValue.y
                    }
                }
        )
    }
}


@Composable
fun DetectMultitouchGestures() {
    var angle by remember { mutableStateOf(0f) }
    var zoom by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    Box(
        Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .graphicsLayer(
                scaleX = zoom,
                scaleY = zoom,
                rotationZ = angle
            )
            .background(Color.Blue)
            .pointerInput {
                detectMultitouchGestures(
                    onGesture = { _, pan, gestureZoom, gestureRotate ->
                        angle += gestureRotate
                        zoom *= gestureZoom
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                )
            }
            .fillMaxSize()
    )
}




@Composable
fun ZoomableDemo() {
    var zoomFactor by remember { mutableStateOf(1f) }
    Row {
        Box(modifier = Modifier
            .preferredSize(100.dp)
            .scale(zoomFactor)
            .background(Color.Red)
            .zoomable {
                zoomFactor *= it
            })
        Text("Zoomable: $zoomFactor")
    }
}

@Composable
fun ZoomableSample() {
    var scale by remember { mutableStateOf(1f) }
    Box(
        Modifier
            .preferredSize(200.dp)
            .clipToBounds()
            .background(Color.LightGray)
            .zoomable { scale *= it }
    ) {
        Text(
            "🍕",
            fontSize = 32.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                )
        )
    }
}

/**
 * Tapping and pressing
 */
@Composable
fun ClickableSample() {
    val count = remember { mutableStateOf(0) }
    // content that you want to make clickable
    Text(
        text = count.value.toString(),
        modifier = Modifier.clickable { count.value += 1 }
    )
}

@Composable
fun PointerInputSnippet() {
    Modifier.pointerInput {
        detectTapGestures(
            onPress = {/* Called when the gesture starts */ },
            onDoubleTap = { /* Called on Double Tap */ },
            onLongPress = { /* Called on Long Press */ },
            onTap = { /* Called on Tap */ }
        )
    }
}

/**
 * Scrolling
 */
@Composable
private fun ScrollBoxes() {
    Column(modifier = Modifier
        .background(Color.LightGray)
        .size(100.dp)
        .verticalScroll(rememberScrollState())
    ) {
        repeat(10) {
            Text("Item $it", modifier = Modifier.padding(2.dp))
        }
    }
}
@Composable
private fun ScrollBoxesSmooth(scope: CoroutineScope = rememberCoroutineScope()) {

    // Smoothly scroll 100px on first composition
    val state = rememberScrollState()
    SideEffect(effect = { scope.launch { state.smoothScrollTo(100f) } })

    Column(modifier = Modifier
        .background(Color.LightGray)
        .size(100.dp)
        .padding(horizontal = 8.dp)
        .verticalScroll(state)
    ) {
        repeat(10) {
            Text("Item $it", modifier = Modifier.padding(2.dp))
        }
    }
}

/**
 * Dragging and swiping
 */

@Composable
fun DraggableDemo() {
    var offsetX by remember { mutableStateOf(0f) }
    Text(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .draggable(
                orientation = Orientation.Horizontal,
                onDrag = { offsetX += it }
            ),
        text = "Drag me!"
    )
}

@Composable
fun DragSnippet2() {
    Box(modifier = Modifier.fillMaxSize() ) {
        val offsetX = remember { mutableStateOf(0f) }
        val offsetY = remember { mutableStateOf(0f) }

        Box(Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .background(Color.Blue)
            .size(50.dp)
            .pointerInput {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    offsetX.value = (offsetX.value + dragAmount.x)
                    offsetY.value = (offsetY.value + dragAmount.y)
                }
            }
        )
    }
}
@Composable
fun ScrollableSample() {
    // actual composable state
    var offset by remember { mutableStateOf(0f) }
    // state for Scrollable, describes how to consume scrolling delta and update offset
    Box(
        Modifier
            .preferredSize(150.dp)
            .scrollable(
                orientation = Orientation.Vertical,
                controller = rememberScrollableController { delta ->
                    offset += delta
                    delta
                }
            )
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(offset.toString())
    }
}

@Composable
fun Nested1() {
    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(48.dp)
    ) {
        Column {
            repeat(3) { Box(modifier = Modifier.padding(48.dp)) { ScrollBoxes() } }
        }
    }
}

@Composable
fun Nested2() {
    val gradient = Brush.verticalGradient(
        listOf(Color.Gray, Color.White), 0.0f, 1000.0f, TileMode.Repeated
    )
    Box(
        modifier = Modifier
            .background(Color.LightGray)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Column {
            repeat(6) {
                Box(
                    modifier = Modifier
                        .preferredHeight(128.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Scroll here", modifier = Modifier
                        .border(12.dp, Color.DarkGray)
                        .background(brush = gradient)
                        .padding(24.dp)
                        .preferredHeight(200.dp))
                }
            }
        }
    }
}

@Composable
fun NestedScrollSample_original() {
    // constructing the box with next that scrolls as long as text within 0 .. 300
    // to support nested scrolling, we need to scroll ourselves, dispatch nested scroll events
    // as we scroll, and listen to potential children when we're scrolling.
    val maxValue = 300f
    val minValue = 0f
    // our state that we update as scroll
    var value by remember { mutableStateOf(maxValue / 2) }
    // create dispatch to dispatch scroll events up to the nested scroll parents
    val nestedScrollDispatcher = remember { NestedScrollDispatcher() }
    // we're going to scroll vertically, so set the orientation to vertical
    val orientation = Orientation.Vertical

    // callback to listen to scroll events and dispatch nested scroll events
    val scrollCallback = remember {
        object : ScrollCallback {
            override fun onScroll(scrollDistance: Float): Float {
                // dispatch prescroll with Y axis since we're going vertical scroll
                val aboveConsumed = nestedScrollDispatcher.dispatchPreScroll(
                    Offset(x = 0f, y = scrollDistance),
                    NestedScrollSource.Drag
                )
                // adjust what we can consume according to pre-scroll
                val available = scrollDistance - aboveConsumed.y
                // let's calculate how much we want to consume and how much is left
                val newTotal = value + available
                val newValue = newTotal.coerceIn(minValue, maxValue)
                val toConsume = newValue - value
                val leftAfterUs = available - toConsume
                // consume ourselves what we need and dispatch "scroll" phase of nested scroll
                value += toConsume
                nestedScrollDispatcher.dispatchPostScroll(
                    Offset(x = 0f, y = toConsume),
                    Offset(x = 0f, y = leftAfterUs),
                    NestedScrollSource.Drag
                )
                // indicate to the old pointer that we handled everything by returning same value
                return scrollDistance
            }

            override fun onStop(velocity: Float) {
                // for simplicity we won't fling ourselves, but we need to respect nested scroll
                // dispatch pre fling
                val velocity2d = Velocity(x = 0f, y = velocity)
                val consumed = nestedScrollDispatcher.dispatchPreFling(velocity2d)
                // now, since we don't fling, we consume 0 (Offset.Zero).
                // Adjust what's left after prefling and dispatch post fling
                val left = velocity2d - consumed
                nestedScrollDispatcher.dispatchPostFling(Velocity.Zero, left)
            }
        }
    }

    // we also want to participate in the nested scrolling, not only dispatching. create connection
    val connection = remember {
        object : NestedScrollConnection {
            // let's assume we want to consume children's delta before them if we can
            // we should do it in pre scroll
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // calculate how much we can take from child
                val oldValue = value
                val newTotal = value + available.y
                val newValue = newTotal.coerceIn(minValue, maxValue)
                val toConsume = newValue - oldValue
                // consume what we want and report back co children can adjust
                value += toConsume
                return Offset(x = 0f, y = toConsume)
            }
        }
    }

    // scrollable parent to which we will dispatch our nested scroll events
    // Since we properly support scrolling above, this parent will scroll even if we scroll inner
    // box (with White background)
    LazyColumn(Modifier.background(Color.Red)) {
        // our box we constructed
        item {
            Box(
                Modifier
                    .size(width = 300.dp, height = 100.dp)
                    .background(Color.White)
                    // add scrolling listening and dispatching
                    .scrollGestureFilter(orientation = orientation, scrollCallback = scrollCallback)
                    // connect self connection and dispatcher to the nested scrolling system
                    .nestedScroll(connection, dispatcher = nestedScrollDispatcher)
            ) {
                // hypothetical scrollable child which we will listen in connection above
                LazyColumn {

                    items(5) {
                        Text(
                            "Magenta text above will change first when you scroll me",
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
                // simply show our value. It will change when we scroll child list above, taking
                // child's scroll delta until we reach maxValue or minValue
                Text(
                    text = value.roundToInt().toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Magenta)
                )
            }
        }
        repeat(100) {
            item {
                Text(
                    "Outer scroll items are Yellow on Red parent",
                    modifier = Modifier
                        .background(Color.Yellow)
                        .padding(5.dp)
                )
            }
        }
    }
}

@Composable
fun NestedScrollSample() {
    var debug by remember { mutableStateOf("Hi") }
    // constructing the box with next that scrolls as long as text within 0 .. 300
    // to support nested scrolling, we need to scroll ourselves, dispatch nested scroll events
    // as we scroll, and listen to potential children when we're scrolling.
    val maxValue = 300f
    val minValue = 0f
    // our state that we update as scroll
    var value by remember { mutableStateOf(0f) }
    // create dispatch to dispatch scroll events up to the nested scroll parents
    val nestedScrollDispatcher = remember { NestedScrollDispatcher() }
    // we're going to scroll vertically, so set the orientation to vertical
    val orientation = Orientation.Vertical

    // callback to listen to scroll events and dispatch nested scroll events
    val scrollCallback = remember {
        object : ScrollCallback {
            override fun onScroll(scrollDistance: Float): Float {
                debug = scrollDistance.toString()
                // dispatch prescroll with Y axis since we're going vertical scroll
                val aboveConsumed = nestedScrollDispatcher.dispatchPreScroll(
                    Offset(x = 0f, y = scrollDistance),
                    NestedScrollSource.Drag
                )
                // adjust what we can consume according to pre-scroll
                val available = scrollDistance - aboveConsumed.y
                // let's calculate how much we want to consume and how much is left
                val newTotal = value + available
                val newValue = newTotal.coerceIn(minValue, maxValue)
                val toConsume = newValue - value
                val leftAfterUs = available - toConsume
                // consume ourselves what we need and dispatch "scroll" phase of nested scroll
                value += toConsume
                nestedScrollDispatcher.dispatchPostScroll(
                    Offset(x = 0f, y = toConsume),
                    Offset(x = 0f, y = leftAfterUs),
                    NestedScrollSource.Drag
                )
                // indicate to the old pointer that we handled everything by returning same value
                return scrollDistance
            }

            override fun onStop(velocity: Float) {
                // for simplicity we won't fling ourselves, but we need to respect nested scroll
                // dispatch pre fling
                val velocity2d = Velocity(x = 0f, y = velocity)
                val consumed = nestedScrollDispatcher.dispatchPreFling(velocity2d)
                // now, since we don't fling, we consume 0 (Offset.Zero).
                // Adjust what's left after prefling and dispatch post fling
                val left = velocity2d - consumed
                nestedScrollDispatcher.dispatchPostFling(Velocity.Zero, left)
            }
        }
    }

    // we also want to participate in the nested scrolling, not only dispatching. create connection
    val connection = remember {
        object : NestedScrollConnection {
            // let's assume we want to consume children's delta before them if we can
            // we should do it in pre scroll
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // calculate how much we can take from child
                val oldValue = value
                val newTotal = value + available.y
                val newValue = newTotal.coerceIn(minValue, maxValue)
                val toConsume = newValue - oldValue
                // consume what we want and report back co children can adjust
                value += toConsume
                return Offset(x = 0f, y = toConsume)
            }
        }
    }
    val state = rememberScrollState()
    Column(
        modifier = Modifier
            .background(Color.DarkGray)
            .fillMaxSize()
            .scrollGestureFilter(scrollCallback, orientation = Orientation.Vertical)
        //.verticalScroll(state)
    ) {
        Box(modifier =
        Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .preferredHeight(128.dp)
            .nestedScroll(connection = connection, nestedScrollDispatcher)
        )
        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxWidth()
                .preferredHeight(2500.dp)
        ) {
            Text(debug)
        }
    }
//
//    // scrollable parent to which we will dispatch our nested scroll events
//    // Since we properly support scrolling above, this parent will scroll even if we scroll inner
//    // box (with White background)
//    LazyColumn(Modifier.background(Color.Red)) {
//        // our box we constructed
//        item {
//            Box(
//                Modifier
//                    .size(width = 300.dp, height = 100.dp)
//                    .background(Color.White)
//                    // add scrolling listening and dispatching
//                    .scrollGestureFilter(orientation = orientation, scrollCallback = scrollCallback)
//                    // connect self connection and dispatcher to the nested scrolling system
//                    .nestedScroll(connection, dispatcher = nestedScrollDispatcher)
//            ) {
//                // hypothetical scrollable child which we will listen in connection above
//                LazyColumn {
//                    items(MutableList(5) { 5 }) {
//                        Text(
//                            "Magenta text above will change first when you scroll me",
//                            modifier = Modifier.padding(5.dp)
//                        )
//                    }
//                }
//                // simply show our value. It will change when we scroll child list above, taking
//                // child's scroll delta until we reach maxValue or minValue
//                Text(
//                    text = value.roundToInt().toString(),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color.Magenta)
//                )
//            }
//        }
//        repeat(100) {
//            item {
//                Text(
//                    "Outer scroll items are Yellow on Red parent",
//                    modifier = Modifier.background(Color.Yellow).padding(5.dp)
//                )
//            }
//        }
//    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    MyApplicationTheme {
        Nested2()
    }
}