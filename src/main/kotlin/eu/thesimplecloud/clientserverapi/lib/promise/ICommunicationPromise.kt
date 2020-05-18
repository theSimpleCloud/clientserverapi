package eu.thesimplecloud.clientserverapi.lib.promise

import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import io.netty.util.concurrent.Promise
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


interface ICommunicationPromise<T : Any> : Promise<T> {

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
    fun addResultListener(listener: (T) -> Unit): ICommunicationPromise<T>

    /**
     * Adds a promise listener to this future.
     * This listener will be called once the future is done
     */
    fun addCompleteListener(listener: (ICommunicationPromise<T>) -> Unit): ICommunicationPromise<T>

    /**
     * Adds a promise listener to this future.
     * This listener will be called once the future is done
     */
    fun addCompleteListener(listener: ICommunicationPromiseListener<T>): ICommunicationPromise<T>

    /**
     * Adds all specified promise listener to this future.
     * The listeners will be called once the future is done
     */
    fun addCommunicationPromiseListeners(vararg listener: ICommunicationPromiseListener<T>): ICommunicationPromise<T>

    /**
     * Executes the specified [function] when this promise is completed successfully.
     * @param function that what shall be happened when this promise is successfully done. If the passed function returns null the returned promise will fail with [NullPointerException]
     * @return a new communication promise completed with the result of the [function] or if this promise fails the returned promise will fail with the same cause.
     */
    fun <R : Any> then(function: (T) -> R?): ICommunicationPromise<R> = thenDelayed(0, TimeUnit.MILLISECONDS, function)

    /**
     * Executes the specified [function] when this promise is completed and the [delay] is over.
     * @param delay the time to wait after this promise is completed before calling the [function]
     * @param timeUnit the unit for the [delay]
     * @param function that what shall be happened when this promise is successfully done. If the passed function returns null the returned promise will fail with [NullPointerException]
     * @return a new communication promise completed with the result of the [function] or if this promise fails the returned promise will fail with the same cause.
     */
    fun <R : Any> thenDelayed(delay: Long, timeUnit: TimeUnit, function: (T) -> R?): ICommunicationPromise<R>

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
    fun getBlocking(): T = syncUninterruptibly().getNow()

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
                coroutine.resume(it.getNow())
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
    fun copyStateFromOtherPromise(otherPromise: ICommunicationPromise<T>)

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

    override fun setSuccess(result: T): ICommunicationPromise<T>

    override fun setFailure(cause: Throwable): ICommunicationPromise<T>


}