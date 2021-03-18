package com.example.jalc.myapplication

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class ExposingEffectsTest {

    @Test
    fun receiveAsFlowTest() {
        val channel = Channel<Int>(capacity = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST)

        runBlocking {
            channel.send(0)
            channel.send(1)
            val observer1 = channel.receiveAsFlow()
            val observer2 = channel.receiveAsFlow()
            channel.send(2)
            channel.close()

            //Buffer replayed to first observer
            assertEquals(observer1.toList(), listOf(1, 2))
            // Second observer does not receive old nor new updates
            assertEquals(observer2.toList().count(), 0)
        }
    }

    @Test
    fun SharedFlowReplayTest() {
        val sharedFlow = MutableSharedFlow<Int>(replay = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST)

        val testResults = mutableListOf<Int>()
        val testResults2 = mutableListOf<Int>()

        runBlockingTest {
            sharedFlow.emit(0)
            sharedFlow.emit(1)
            sharedFlow.emit(2)
            val job1 = launch {
                sharedFlow.toList(testResults)
            }
            sharedFlow.emit(3)
            val job2 = launch {
                sharedFlow.toList(testResults2)
            }

            job1.cancel()
            job2.cancel()
            // The first observer gets the full buffer and new updates
            assertEquals(listOf(1, 2, 3), testResults)
            // Second observer gets the full buffer
            assertEquals(listOf(2, 3), testResults2)
        }
    }
    @Test
    fun SharedFlow0ReplayTest() {
        val sharedFlow = MutableSharedFlow<Int>(replay = 0)

        val testResults = mutableListOf<Int>()
        val testResults2 = mutableListOf<Int>()

        runBlockingTest {
            sharedFlow.emit(0)
            sharedFlow.emit(1)
            sharedFlow.emit(2)
            val job1 = launch {
                sharedFlow.toList(testResults)
            }
            sharedFlow.emit(3)
            val job2 = launch {
                sharedFlow.toList(testResults2)
            }

            job1.cancel()
            job2.cancel()
            // The first observer only gets new updates
            assertEquals(listOf(3), testResults)
            // Second observer only gets new updates - none
            assertEquals(emptyList<Int>(), testResults2)
        }
    }

    @Test
    fun channelShareInTest() {

        val scope = TestCoroutineScope()
        val _channel = Channel<Int>(capacity = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST)
        val exposedFlow: SharedFlow<Int> = _channel
            .consumeAsFlow().shareIn(scope, SharingStarted.WhileSubscribed())

        val testResults = mutableListOf<Int>()
        val testResults2 = mutableListOf<Int>()

        runBlockingTest {
            _channel.send(0)
            _channel.send(1)
            _channel.send(2)
            val observer1 = exposedFlow

            val job1 = launch {
                observer1.toList(testResults)
            }

            val observer2 = exposedFlow

            val job2 = launch {
                observer2.toList(testResults2)
            }

            _channel.send(3)

            job1.cancel()
            job2.cancel()

            // The first observer gets buffer + new updates
            assertEquals(listOf(1, 2, 3), testResults)
            // Second observer only gets new updates
            assertEquals(listOf(3), testResults2)
        }
    }
}
