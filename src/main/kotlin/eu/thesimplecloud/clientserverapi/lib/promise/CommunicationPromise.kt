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

package eu.thesimplecloud.clientserverapi.lib.promise

import eu.thesimplecloud.clientserverapi.lib.promise.exception.CompletedWithNullException
import eu.thesimplecloud.clientserverapi.lib.promise.exception.PromiseCreationException
import eu.thesimplecloud.clientserverapi.lib.promise.timout.CommunicationPromiseTimeoutHandler
import io.netty.util.concurrent.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CommunicationPromise<out T : Any>(
    private val timeout: Long = 200,
    val enableTimeout: Boolean = true
) : DefaultPromise<@UnsafeVariance T>(), ICommunicationPromise<T> {

    val creationError = PromiseCreationException()

    constructor(result: T, timeout: Long = 200) : this(timeout) {
        trySuccess(result)
    }

    constructor(throwable: Throwable, timeout: Long = 200) : this(timeout) {
        tryFailure(throwable)
    }

    constructor(enableTimeout: Boolean) : this(200, enableTimeout)

    init {
        if (enableTimeout)
            CommunicationPromiseTimeoutHandler.INSTANCE.handleTimeout(this, timeout)
    }

    override fun addResultListener(listener: (T) -> Unit): ICommunicationPromise<T> {
        addCompleteListener { promise ->
            if (promise.isSuccess) {
                listener(promise.get())
            }
        }
        return this
    }

    override fun addCompleteListener(listener: (ICommunicationPromise<T>) -> Unit): ICommunicationPromise<T> {
        addCompleteListener(object : ICommunicationPromiseListener<T> {
            override fun operationComplete(future: ICommunicationPromise<@UnsafeVariance T>) {
                try {
                    listener(future)
                } catch (e: Exception) {
                    GlobalScope.launch { throw e }
                }
            }
        })
        return this
    }

    override fun addCompleteListener(listener: ICommunicationPromiseListener<@UnsafeVariance T>): ICommunicationPromise<T> {
        super.addListener(listener)
        return this
    }

    override fun addCommunicationPromiseListeners(vararg listener: ICommunicationPromiseListener<@UnsafeVariance T>): ICommunicationPromise<T> {
        super.addListeners(*listener)
        return this
    }

    override fun <R : Any> thenDelayed(delay: Long, timeUnit: TimeUnit, function: (T) -> R?): ICommunicationPromise<R> {
        val newPromise = CommunicationPromise<R>(this.timeout + timeUnit.toMillis(delay), this.isTimeoutEnabled())
        this.addCompleteListener {
            GlobalEventExecutor.INSTANCE.schedule({
                if (this.isSuccess) {
                    try {
                        val functionValue = function(this.get())
                        if (functionValue == null) {
                            newPromise.tryFailure(CompletedWithNullException())
                        } else {
                            newPromise.trySuccess(functionValue)
                        }
                    } catch (ex: Exception) {
                        newPromise.tryFailure(ex)
                    }
                } else {
                    newPromise.tryFailure(this.cause())
                }
            }, delay, timeUnit)
        }
        return newPromise
    }

    override fun getTimeout(): Long = this.timeout

    override fun isTimeoutEnabled(): Boolean = this.enableTimeout

    override fun copyStateFromOtherPromise(otherPromise: ICommunicationPromise<@UnsafeVariance T>) {
        otherPromise.addCompleteListener {
            if (it.isSuccess) {
                this.trySuccess(it.get())
            } else {
                this.tryFailure(it.cause())
            }
        }
    }

    override fun addListener(listener: GenericFutureListener<out Future<in T>>?): ICommunicationPromise<T> {
        super.addListener(listener)
        return this
    }

    override fun addListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): ICommunicationPromise<T> {
        super.addListeners(*listeners)
        return this
    }

    override fun removeListener(listener: GenericFutureListener<out Future<in T>>?): ICommunicationPromise<T> {
        super.removeListener(listener)
        return this
    }

    override fun removeListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): ICommunicationPromise<T> {
        super.removeListeners(*listeners)
        return this
    }

    override fun sync(): ICommunicationPromise<T> {
        super.sync()
        return this
    }

    override fun syncUninterruptibly(): ICommunicationPromise<T> {
        super.syncUninterruptibly()
        return this
    }

    override fun await(): ICommunicationPromise<T> {
        super.await()
        return this
    }

    override fun awaitUninterruptibly(): ICommunicationPromise<T> {
        super.awaitUninterruptibly()
        return this
    }

    override fun executor(): EventExecutor {
        return GlobalEventExecutor.INSTANCE
    }

    override fun setSuccess(result: @UnsafeVariance T): ICommunicationPromise<T> {
        super.setSuccess(result)
        return this
    }

    override fun setFailure(cause: Throwable): ICommunicationPromise<T> {
        super.setFailure(cause)
        return this
    }

    override fun combine(communicationPromise: ICommunicationPromise<*>, sumUpTimeouts: Boolean): ICommunicationPromise<Unit> {
        return combineAllToUnitPromise(listOf(communicationPromise, this), sumUpTimeouts)
    }

    override fun combineAll(promises: List<ICommunicationPromise<*>>, sumUpTimeouts: Boolean): ICommunicationPromise<Unit> {
        return combineAllToUnitPromise(promises.union(listOf(this)), sumUpTimeouts)
    }

    override fun addFailureListener(listener: (Throwable) -> Unit): ICommunicationPromise<T> {
        addCompleteListener { promise ->
            promise.cause()?.let { listener(it) }
        }
        return this
    }

    //override methods to prevent completing with nulls

    override fun tryFailure(cause: Throwable): Boolean {
        return super.tryFailure(cause)
    }

    override fun trySuccess(result: @UnsafeVariance T): Boolean {
        return super.trySuccess(result)
    }

    companion object {
        fun combineAllToUnitPromise(promises: Collection<ICommunicationPromise<*>>, sumUpTimeouts: Boolean = true): ICommunicationPromise<Unit> {
            val disableTimeout = promises.any { !it.isTimeoutEnabled() }
            val timeout = if (sumUpTimeouts) promises.sumBy { it.getTimeout().toInt() }.toLong() else 400
            val unitPromise = CommunicationPromise<Unit>(timeout, !disableTimeout)
            if (promises.isEmpty()) unitPromise.trySuccess(Unit)
            promises.forEach { promise ->
                promise.addCompleteListener { _ ->
                    if (promises.all { it.isDone }) {
                        unitPromise.trySuccess(Unit)
                    }
                }
            }
            return unitPromise
        }

        /**
         * Returns a new promise completed with the [value] or if the [value] is null one failed with the specified [exception]
         */
        fun <T : Any> ofNullable(value: T?, exception: Throwable): ICommunicationPromise<T> {
            return if (value == null) failed(exception) else of(value)
        }

        /**
         * Returns a new promise completed with the specified [value]
         */
        fun <T : Any> of(value: T): ICommunicationPromise<T> {
            return CommunicationPromise<T>(value)
        }

        /**
         * Returns a new promise failed with the specified [throwable]
         */
        fun <T : Any> failed(throwable: Throwable): ICommunicationPromise<T> {
            return CommunicationPromise<T>(throwable)
        }

        /**
         * Runs the specified [block] async and return it result.
         */
        fun <R : Any> runAsync(block: suspend CoroutineScope.() -> R): ICommunicationPromise<R> {
            return runAsync(-1, block)
        }

        /**
         * Runs the specified [block] async and return it result.
         */
        fun <R : Any> runAsync(timeout: Long, block: suspend CoroutineScope.() -> R): ICommunicationPromise<R> {
            val timeoutEnabled = timeout > 0
            val promise = CommunicationPromise<R>(timeout, timeoutEnabled)
            val job = GlobalScope.launch {
                try {
                    promise.trySuccess(block())
                } catch (ex: Exception) {
                    promise.tryFailure(ex)
                }
            }
            promise.addCompleteListener { if (!job.isCompleted) job.cancel() }
            return promise
        }

        val UNIT_PROMISE = of(Unit)
    }
}