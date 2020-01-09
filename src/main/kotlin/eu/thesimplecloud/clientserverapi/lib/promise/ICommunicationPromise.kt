package eu.thesimplecloud.clientserverapi.lib.promise

import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import io.netty.util.concurrent.Promise
import java.util.concurrent.TimeUnit


interface ICommunicationPromise<T> : Promise<T> {

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
    fun addCompleteListener(listener: ICmmunicationPromiseListener<T>): ICommunicationPromise<T>

    /**
     * Adds all specified promise listener to this future.
     * The listeners will be called once the future is done
     */
    fun addCommunicationPromiseListeners(vararg listener: ICmmunicationPromiseListener<T>): ICommunicationPromise<T>

    /**
     * Executes the specified [function] when this promise is completed successfully.
     */
    fun thenAccept(function: (T) -> Unit) = thenAcceptDelayed(0, TimeUnit.MILLISECONDS, function)


    /**
     * Executes the specified [function] when this promise is completed successfully.
     * @param function that what shall be happened when this promise is successfully done.
     * @return a new communication promise completed with the result of the [function] or if this promise fails the returned promise will fail with the same cause.
     */
    fun <R> then(function: (T) -> R): ICommunicationPromise<R> = thenDelayed(0, TimeUnit.MILLISECONDS, function)

    /**
     * Executes the specified [function] when this promise is completed and the [delay] is over.
     * @param delay the time to wait after this promise is completed before calling the [function]
     * @param timeUnit the unit for the [delay]
     * @return a new communication promise completed with the result of the [function] or if this promise fails the returned promise will fail with the same cause.
     */
    fun thenAcceptDelayed(delay: Long, timeUnit: TimeUnit, function: (T) -> Unit)

    /**
     * Executes the specified [function] when this promise is completed and the [delay] is over.
     * @param delay the time to wait after this promise is completed before calling the [function]
     * @param timeUnit the unit for the [delay]
     * @return a new communication promise completed with the result of the [function] or if this promise fails the returned promise will fail with the same cause.
     */
    fun <R> thenDelayed(delay: Long, timeUnit: TimeUnit, function: (T) -> R): ICommunicationPromise<R>

    /**
     * Returns the timeout of this promise
     */
    fun getTimeout(): Long

    /**
     * Copies the information of the specified promise to this promise when the specified promise completes.
     */
    fun copyPromiseConfigurationOnComplete(otherPromise: ICommunicationPromise<T>)

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