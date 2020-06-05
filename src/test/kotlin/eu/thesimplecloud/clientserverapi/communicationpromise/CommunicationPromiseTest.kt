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

import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.flatten
import org.junit.Test
import java.util.concurrent.TimeUnit

class CommunicationPromiseTest {

    @Test
    fun test() {
        val promise1 = CommunicationPromise<Int>()
        val promise2 = CommunicationPromise<Int>()
        promise1.then { println("then: $it") }
        val promise = promise1.then {
            println("promise1: $it")
            promise2
        }
        promise1.thenDelayed(7000, TimeUnit.MILLISECONDS) {
            println("promise1300: $it")
        }
        promise.flatten().then { println("test: $it") }
        promise1.then { println("testttt: $it") }
        promise1.trySuccess(111)
        promise2.trySuccess(3333)
        println(2222)
        Thread.sleep(10000)
    }

    @Test
    fun test2() {
        println("2-test2--------------------------------")
        val promise1 = CommunicationPromise<Int>()
        //val promise2 = CommunicationPromise<Int>()
        promise1.then { result -> println("2-test-${result}") }.then { println("2-test: " + it.toString()) }.addFailureListener { println("2-failed: ${it::class.java.simpleName}") }
        //promise1.then { println("2-success-test: $it") }.addFailureListener { println("2-failed: ${it::class.java.simpleName}") }
        promise1.trySuccess(6433)
        Thread.sleep(200)
    }

}