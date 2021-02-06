/*
 * MIT License
 *
 * Copyright (C) 2020 Frederick Baier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.clientserverapi.communicationpromise

import eu.thesimplecloud.clientserverapi.lib.promise.*
import eu.thesimplecloud.clientserverapi.lib.promise.exception.CompletedWithNullException
import eu.thesimplecloud.clientserverapi.utils.TestException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class CommunicationPromiseTest {

    @Test
    fun promise_then_test() {
        val promise1 = CommunicationPromise<Int>(1, 200)
        val secondPromise = promise1.then { it + 2 }
        assertEquals(3, secondPromise.getBlocking())

    }

    @Test
    fun promise_then_chain_test() {
        val promise1 = CommunicationPromise<Int>(1, 200)
        val secondPromise = promise1.then { it + 2 }
                .then { it + 2 }
                .then { it + 2 }
        assertEquals(7, secondPromise.getBlocking())
    }

    @Test
    fun promise_fail_test() {
        val promise1 = CommunicationPromise.failed<Int>(TestException())
        assertTrue( promise1.cause() is TestException)

    }

    @Test
    fun promise_fail_chain_test() {
        val failedPromise = CommunicationPromise.failed<Int>(TestException())
        val promise = failedPromise.then { it + 3 }.then { it + 2 }
        promise.awaitUninterruptibly()
        assertTrue(promise.cause() is TestException)

    }

    @Test
    fun promise_flatten_test() {
        val innerFailedPromise = CommunicationPromise.failed<Int>(TestException())
        val outerPromise = CommunicationPromise(Unit)
        val flattenFailedPromise = outerPromise.then { innerFailedPromise }.flatten()
        flattenFailedPromise.awaitUninterruptibly()
        assertTrue(flattenFailedPromise.cause() is TestException)

    }

    @Test
    fun promise_blocking_success_test() {
        val promise = CommunicationPromise.of(Unit)
        assertEquals(Unit, promise.getBlocking())
    }

    @Test
    fun promise_blocking_failure_test() {
        val promise = CommunicationPromise.failed<Int>(TestException())
        assertThrows(TestException::class.java) { promise.getBlocking() }
    }

    @Test
    fun promise_blocking_or_null_success_test() {
        val promise = CommunicationPromise.of(Unit)
        assertEquals(Unit, promise.getBlockingOrNull())
    }

    @Test
    fun promise_blocking_or_null_failure_test() {
        val promise = CommunicationPromise.failed<Int>(TestException())
        assertNull(promise.getBlockingOrNull())
    }

    @Test
    fun to_unit_promise_success_test() {
        val promise = CommunicationPromise.of(2)
        assertEquals(Unit, promise.toUnitPromise().getBlocking())
    }

    @Test
    fun to_unit_promise_failure_test() {
        val promise = CommunicationPromise.failed<Int>(TestException())
        assertTrue(promise.toUnitPromise().awaitUninterruptibly().cause() is TestException)
    }

    @Test
    fun to_unit_promise_later_success_test() {
        val promise = CommunicationPromise<Int>(timeout = 500)
        GlobalScope.launch {
            delay(100)
            promise.trySuccess(2)
        }
        assertEquals(Unit, promise.toUnitPromise().getBlocking())
    }

    @Test
    fun to_unit_promise_later_failure_test() {
        val promise = CommunicationPromise<Int>(timeout = 500)
        GlobalScope.launch {
            delay(100)
            promise.tryFailure(TestException())
        }
        assertTrue(promise.toUnitPromise().awaitUninterruptibly().cause() is TestException)
    }

    @Test
    fun promise_combine_test() {
        val promiseList = (0 until 10).map { createLaterCompletingPromise() }
        val combinedPromise = promiseList.combineAllPromises()
        combinedPromise.awaitUninterruptibly()
        assertTrue(promiseList.all { it.isSuccess })
    }

    @Test
    fun promise_list_test() {
        val promiseList = (0 until 10).map { createLaterCompletingPromise() }
        val listPromise = promiseList.toListPromise()
        listPromise.awaitUninterruptibly()
        //all promises are completing with 4
        assertEquals(10 * 4, listPromise.getBlocking().filterNotNull().sum())
        assertTrue(promiseList.all { it.isSuccess })
    }

    private fun createLaterCompletingPromise(): ICommunicationPromise<Int> {
        val promise = CommunicationPromise<Int>(timeout = 500)
        GlobalScope.launch {
            delay(50 + Random.nextInt(100).toLong())
            promise.trySuccess(4)
        }
        return promise
    }


    @Test
    fun promise_complete_with_null() {
        val promise = CommunicationPromise.of(Unit)
        val nullPromise = promise.then { null }
        nullPromise.awaitUninterruptibly()
        assertTrue(nullPromise.cause() is CompletedWithNullException)
    }

}