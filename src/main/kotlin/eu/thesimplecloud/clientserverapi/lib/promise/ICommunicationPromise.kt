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
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import io.netty.util.concurrent.Promise
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


interface ICommunicationPromise<out T : Any> : Promise<@UnsafeVariance T> {

    /**
     * Combines this promise with the specified promise.
     * @return a new promise that is completed once both (this promise and the specified one) are completed.
     */
    fun combine(communicationPromise: ICommunicationPromise<*>, sumUpTimeouts: Boolean = false): ICommunicationPromise<Unit>

    /**
     * Combines this promise with the specified promises.
     * @return a new promise that is completed once this and all specified promises are completed.
     */
    fun combineAll(promises: List<ICommunicationPromise<*>>, sumUpTimeouts: Boolean = false): ICommunicationPromise<Unit>

    /**
     * Adds a failure listener to this promise.
     * The listener will be notified when the promise fails.
     */
    fun addFailureListener(listener: (Throwable) -> Unit): ICommunicationPromise<T>

    /**
     * Adds a result listener to this future.
     * The listener will be called once the result is ready.
     * This listener will not be notified when the promise completes successfully.
     */
    fun addResultListener(listener: (@UnsafeVariance T) -> Unit): ICommunicationPromise<T>

    /**
     * Adds a promise listener to this future.
     * This listener will be called once the future is done
     */
    fun addCompleteListener(listener: (ICommunicationPromise<@UnsafeVariance T>) -> Unit): ICommunicationPromise<T>

    /**
     * Adds a promise listener to this future.
     * This listener will be called once the future is done
     */
    fun addCompleteListener(listener: ICommunicationPromiseListener<@UnsafeVariance T>): ICommunicationPromise<T>

    /**
     * Adds all specified promise listener to this future.
     * The listeners will be called once the future is done
     */
    fun addCommunicationPromiseListeners(vararg listener: ICommunicationPromiseListener<@UnsafeVariance T>): ICommunicationPromise<T>

    /**
     * Executes the specified [function] when this promise is completed successfully.
     * @param function that what shall be happened when this promise is successfully done. If the passed function returns null the returned promise will fail with [CompletedWithNullException]
     * @return a new communication promise completed with the result of the [function] or if this promise fails the returned promise will fail with the same cause.
     */
    fun <R : Any> then(function: (@UnsafeVariance T) -> R?): ICommunicationPromise<R> = thenDelayed(0, TimeUnit.MILLISECONDS, function)

    /**
     * Executes the specified [function] when this promise is completed and the [delay] is over.
     * @param delay the time to wait after this promise is completed before calling the [function]
     * @param timeUnit the unit for the [delay]
     * @param function that what shall be happened when this promise is successfully done. If the passed function returns null the returned promise will fail with [CompletedWithNullException]
     * @return a new communication promise completed with the result of the [function] or if this promise fails the returned promise will fail with the same cause.
     */
    fun <R : Any> thenDelayed(delay: Long, timeUnit: TimeUnit, function: (@UnsafeVariance T) -> R?): ICommunicationPromise<R>

    /**
     * Returns the timeout of this promise
     */
    fun getTimeout(): Long

    /**
     * Returns whether the timeout is enabled for this promise
     */
    fun isTimeoutEnabled(): Boolean

    /**
     * Waits for the result and returns it. If an error occurs it will be thrown.
     */
    fun getBlocking(): T = syncUninterruptibly().getNow()!!

    /**
     * Waits for the result and returns it. If an error occurs this method will return null.
     */
    fun getBlockingOrNull(): T? = awaitUninterruptibly().getNow()

    /**
     * Returns this promise as unit promise
     */
    fun toUnitPromise(): ICommunicationPromise<Unit> {
        val promise = CommunicationPromise<Unit>()
        promise.copyStateFromOtherPromiseToUnitPromise(this)
        return promise
    }

    /**
     * Waits until this promise completes
     * @return this promise
     */
    suspend fun awaitCoroutine(): ICommunicationPromise<T> = suspendCancellableCoroutine { coroutine ->
        addCompleteListener {
            coroutine.resume(this)
        }
    }

    /**
     * Returns the result of this promise in coroutines
     */
    suspend fun getCoroutineBlocking(): T = suspendCancellableCoroutine { coroutine ->
        addCompleteListener {
            if (it.isSuccess) {
                coroutine.resume(it.getNow()!!)
            } else {
                coroutine.resumeWithException(it.cause())
            }
        }
    }

    /**
     * Returns the result of this promise or null if the promise fails.
     */
    suspend fun getCoroutineBlockingOrNull(): T? = suspendCancellableCoroutine { coroutine ->
        addCompleteListener {
            if (it.isSuccess) {
                coroutine.resume(it.getNow())
            } else {
                coroutine.resume(null)
            }
        }
    }

    /**
     * Copies the information of the specified promise to this promise when the specified promise completes.
     */
    fun copyStateFromOtherPromise(otherPromise: ICommunicationPromise<@UnsafeVariance T>)

    /**
     * Throws the exception if one occurs
     */
    fun throwFailure(): ICommunicationPromise<T> {
        this.addFailureListener { throwable -> throw throwable }
        return this
    }

    override fun addListener(listener: GenericFutureListener<out Future<in T>>?): ICommunicationPromise<T>

    override fun addListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): ICommunicationPromise<T>

    override fun removeListener(listener: GenericFutureListener<out Future<in T>>?): ICommunicationPromise<T>

    override fun removeListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): ICommunicationPromise<T>

    override fun sync(): ICommunicationPromise<T>

    override fun syncUninterruptibly(): ICommunicationPromise<T>

    override fun await(): ICommunicationPromise<T>

    override fun awaitUninterruptibly(): ICommunicationPromise<T>

    override fun setSuccess(result: @UnsafeVariance T): ICommunicationPromise<T>

    override fun setFailure(cause: Throwable): ICommunicationPromise<T>

    override fun getNow(): T?

}