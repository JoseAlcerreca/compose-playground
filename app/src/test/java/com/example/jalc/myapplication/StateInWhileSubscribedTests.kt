package com.example.jalc.myapplication

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class StateInWhileSubscribedTests {

    @Test
    fun testCold() {
        val testResults1 = mutableListOf<String>()
        val testResults2 = mutableListOf<String>()
        val testResults3 = mutableListOf<String>()

        var combineCalls = 0

        val scope = TestCoroutineScope()
        scope.runBlockingTest {
    //        val f1 = MutableStateFlow("HelloState") //flowOf("Hello")
            val f1 = MutableStateFlow("A")
            val f2 = MutableStateFlow("0")

            //val scope = CoroutineScope(this.coroutineContext)
            val f3 = f1.combine(f2) { e1, e2 ->
                println("Long calculation executing: $e1 + $e2")
                combineCalls++
                "$e1+$e2"
            }.stateIn(scope = scope, started = SharingStarted.WhileSubscribed(2000), initialValue = "Initcombine")

            println("Start collecting")
            val job1 = launch(CoroutineName("My-Coroutine1")) {
                f3.collect {
                    testResults1.add(it)
                }
            }

            f2.emit("1")
            yield()
            job1.cancel()

            println(testResults1)
            yield()
            println("Cancel collection, collect again:")
            val job2 = launch(CoroutineName("My-Coroutine2")) {
                f3.collect {
                    testResults2.add(it)
                }
            }
            println(testResults2)
            println("Cancel collection,")

            job2.cancel()
            println("Change input:")

            f2.emit("2")

            println("Collect again:")
            val job3 = launch(CoroutineName("My-Coroutine3")) {
                f3.collect {
                    testResults3.add(it)
                }
            }
            job3.cancel()

            println(testResults3)
            println("Change input but don't collect after the stopTimeoutMillis")
            delay(2001)

            f2.emit("3")
            yield()
        }

        assertEquals(listOf("A+0", "A+1"), testResults1)
        assertEquals(listOf("A+1"), testResults2)
        assertEquals(listOf("A+2"), testResults3)

        // Exactly 3 calls to combineCalls
        assertEquals(3, combineCalls)
    }

    @Test
    fun testCold_Zerotimeout() {
        val testResults1 = mutableListOf<String>()
        val testResults2 = mutableListOf<String>()
        val testResults3 = mutableListOf<String>()

        var combineCalls = 0

        val scope = TestCoroutineScope()
        scope.runBlockingTest {
            val f1 = MutableStateFlow("A")
            val f2 = MutableStateFlow("0")

            //val scope = CoroutineScope(this.coroutineContext)
            val f3 = f1.combine(f2) { e1, e2 ->
                yield()
                println("Long calculation executing: $e1 + $e2")
                combineCalls++
                "$e1+$e2"
            }.stateIn(scope = scope, started = SharingStarted.WhileSubscribed(0), initialValue = "Initcombine")

            println("Start collecting")
            val job1 = launch(CoroutineName("My-Coroutine1")) {
                f3.collect {
                    testResults1.add(it)
                }
            }

            f2.emit("1")
            yield()
            job1.cancel()

            println(testResults1)
            yield()
            println("Cancel collection, collect again:")
            val job2 = launch(CoroutineName("My-Coroutine2")) {
                f3.collect {
                    println("Collecting $it")
                    testResults2.add(it)
                }
            }
            println(testResults2)
            println("Cancel collection,")

            yield()
            delay(1000)
            job2.cancel()
            println("Change input:")

            f2.emit("2")
            yield()
            println("Collect again:")
            val job3 = launch(CoroutineName("My-Coroutine3")) {
                f3.collect {
                    testResults3.add(it)
                }
            }
            job3.cancel()

            println(testResults3)
            println("Change input but don't collect after the stopTimeoutMillis")
            delay(2001)

            f2.emit("3")
            yield()
        }

        assertEquals(listOf("A+0", "A+1"), testResults1)
        assertEquals(listOf("A+1"), testResults2)
        assertEquals(listOf("A+2"), testResults3)

        // Exactly 4 calls to combineCalls because of f2.emit("3") and zero timeout
        assertEquals(4, combineCalls)
    }

    @Test
    fun test_simple()  {
        val scope = TestCoroutineScope()
        scope.runBlockingTest {
            var upstreamFlowExecuted = 0

            val myFlow = flow {
                upstreamFlowExecuted++
                println("Upstream flow executing for $upstreamFlowExecuted time")
                println("getting ready to emit 1")
                emit(1)
                println("getting ready to emit 2")
                emit(2)
                println("getting ready to emit 3")
                emit(3)
                println("getting ready to emit 4")
                emit(4)
                println()
            }

            val myStateFlow = myFlow.stateIn(scope, SharingStarted.WhileSubscribed(0), initialValue = 0)

            println("Start collecting")
            val job1 = launch(CoroutineName("My-Coroutine1")) {
                myStateFlow.collect()
            }
            yield()
            job1.cancel()
            delay(2000) // just to double make sure that number of subscribers goes to zero

            println("Start collecting again")
            val job2 = launch(CoroutineName("My-Coroutine2")) {
                myStateFlow.collect()
            }
            yield()
            job2.cancel()
            assertEquals(2, upstreamFlowExecuted)
        }
        scope.cleanupTestCoroutines()
    }

    @Test
    fun test_simple2()  {
        val scope = TestCoroutineScope()
        scope.runBlockingTest {
            var upstreamFlowExecuted = 0

            val myFlow = flow {
                upstreamFlowExecuted++
                println("Upstream flow executing for $upstreamFlowExecuted time")
                println("getting ready to emit 1")
                emit(1)
                println("getting ready to emit 2")
                emit(2)
                println("getting ready to emit 3")
                emit(3)
                println("getting ready to emit 4")
                emit(4)
                println()
            }

            val myStateFlow = myFlow.stateIn(scope, SharingStarted.WhileSubscribed(5000), initialValue = 0)

            println("Start collecting")
            val job1 = launch(CoroutineName("My-Coroutine1")) {
                myStateFlow.collect()
            }
            yield()
            job1.cancel()
            delay(2000) // just to double make sure that number of subscribers goes to zero

            println("Start collecting again")
            val job2 = launch(CoroutineName("My-Coroutine2")) {
                myStateFlow.collect()
            }
            yield()
            job2.cancel()
            assertEquals(1, upstreamFlowExecuted)
        }
        scope.cleanupTestCoroutines()
    }

    @Test
    fun testColdFlows() {

        val scope = TestCoroutineScope()
        scope.runBlockingTest {
            val coldFlow1 = flow {
                emit("1")
                emit("2")
            }
            val coldFlow2 = flow {
                emit("A")
                emit("B")
            }

            val combination = coldFlow1.combine(coldFlow2) { e1, e2 ->
                println("combine executing for $e1+$e2")
                "$e1+$e2 "
            }

            println("Start collecting")
            val job1 = launch(CoroutineName("My-Coroutine1")) {
                combination.collect()
            }
            yield()
            job1.cancel()

            println("Start collecting again")
            val job2 = launch(CoroutineName("My-Coroutine2")) {
                combination.collect()
            }
            yield()
            job2.cancel()


        }
    }
}
