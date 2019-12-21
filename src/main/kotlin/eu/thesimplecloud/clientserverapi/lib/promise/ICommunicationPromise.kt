package eu.thesimplecloud.clientserverapi.lib.promise

import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import io.netty.util.concurrent.Promise


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
    fun addResultListener(listener: (T?) -> Unit): ICommunicationPromise<T>

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
     * Executed the specified [predicate] when this promise is completed successfully.
     */
    fun thenAccept(predicate: (T?) -> Unit)

    /**
     * Executed the specified [predicate] when this promise is completed successfully and its result is not null.
     */
    fun thenAcceptNonNull(predicate: (T) -> Unit)

    /**
     * Executed the specified [predicate] when this promise is completed successfully.
     * @return a new communication promise completed with the result of the [predicate] or if this promise fails the returned promise will fail with the same cause.
     */
    fun <R> then(predicate: (T?) -> R): ICommunicationPromise<R>

    /**
     * Executed the specified [predicate] when this promise is completed successfully and its result is not null.
     * @return a new communication promise completed with the result of the [predicate] or if this promise fails the returned promise will fail with the same cause.
     */
    fun <R> thenNonNull(predicate: (T) -> R): ICommunicationPromise<R>

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


}