package eu.thesimplecloud.clientserverapi.lib.promise

import eu.thesimplecloud.clientserverapi.lib.promise.excpetion.PromiseCreationException
import eu.thesimplecloud.clientserverapi.lib.promise.timout.CommunicationPromiseTimeoutHandler
import io.netty.util.concurrent.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CommunicationPromise<T : Any>(private val timeout: Long = 200, val enableTimeout: Boolean = true) : DefaultPromise<T>(), ICommunicationPromise<T> {

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
            override fun operationComplete(future: ICommunicationPromise<T>) {
                try {
                    listener(future)
                } catch (e: Exception) {
                    GlobalScope.launch { throw e }
                }
            }
        })
        return this
    }

    override fun addCompleteListener(listener: ICommunicationPromiseListener<T>): ICommunicationPromise<T> {
        super.addListener(listener)
        return this
    }

    override fun addCommunicationPromiseListeners(vararg listener: ICommunicationPromiseListener<T>): ICommunicationPromise<T> {
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
                            newPromise.tryFailure(NullPointerException())
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

    override fun copyStateFromOtherPromise(otherPromise: ICommunicationPromise<T>) {
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

    override fun setSuccess(result: T): ICommunicationPromise<T> {
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

    override fun trySuccess(result: T): Boolean {
        return super.trySuccess(result)
    }

    companion object {
        fun combineAllToUnitPromise(promises: Collection<ICommunicationPromise<*>>, sumUpTimeouts: Boolean = false): ICommunicationPromise<Unit> {
            val timeout = if (sumUpTimeouts) promises.sumBy { it.getTimeout().toInt() }.toLong() else 400
            val unitPromise = CommunicationPromise<Unit>(timeout)
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
        fun <R : Any> runAsync(block: () -> R): ICommunicationPromise<R> {
            val promise = CommunicationPromise<R>(enableTimeout = false)
            GlobalScope.launch {
                try {
                    promise.trySuccess(block())
                } catch (ex: Exception) {
                    promise.tryFailure(ex)
                }
            }
            return promise
        }
    }
}