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

package eu.thesimplecloud.clientserverapi.lib.promise.timout

import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.excpetion.PromiseTimeoutException
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

class CommunicationPromiseTimeoutHandler private constructor() : ICommunicationPromiseTimeoutHandler {

    companion object {
        val INSTANCE: ICommunicationPromiseTimeoutHandler = CommunicationPromiseTimeoutHandler()
    }

    /**
     * This map contains a promise and its timeout timestamp in milliseconds
     */
    private val promiseToTimeout = ConcurrentHashMap<ICommunicationPromise<*>, Long>()

    init {
        startThread()
    }


    private fun startThread() {
        thread(start = true, isDaemon = true) {
            while (true) {
                val timedOutPromises = promiseToTimeout.entries.filter { it.value < System.currentTimeMillis() }.map { it.key }
                timedOutPromises.forEach { it.tryFailure(PromiseTimeoutException("Timed out", (it as CommunicationPromise<*>).creationError)) }
                timedOutPromises.forEach { promiseToTimeout.remove(it) }
                Thread.sleep(10)
            }
        }
    }

    override fun handleTimeout(communicationPromise: ICommunicationPromise<*>, timeout: Long) {
        this.promiseToTimeout[communicationPromise] = System.currentTimeMillis() + timeout
    }

}