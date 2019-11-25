package eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise

import com.sun.corba.se.impl.ior.iiop.IIOPAddressClosureImpl
import io.netty.util.concurrent.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CommunicationPromise<T>() : DefaultPromise<T>(), ICommunicationPromise<T> {

    constructor(result: T): this() {
        trySuccess(result)
    }

    constructor(throwable: Throwable): this() {
        tryFailure(throwable)
    }

    override fun addResultListener(listener: (T?) -> Unit): ICommunicationPromise<T> {
        addCompleteListener { promise ->
            if (promise.isSuccess) {
                listener(promise.getNow())
            }
        }
        return this
    }

    override fun addCompleteListener(listener: (ICommunicationPromise<T>) -> Unit): ICommunicationPromise<T> {
        addCompleteListener(object : ICmmunicationPromiseListener<T> {
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

    override fun addCompleteListener(listener: ICmmunicationPromiseListener<T>): ICommunicationPromise<T> {
        super.addListener(listener)
        return this
    }

    override fun addCommunicationPromiseListeners(vararg listeners: ICmmunicationPromiseListener<T>): ICommunicationPromise<T> {
        super.addListeners(*listeners)
        return this
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

    override fun combine(communicationPromise: ICommunicationPromise<*>): ICommunicationPromise<Unit> {
        return combineAllToUnitPromise(listOf(communicationPromise, this))
    }

    override fun combineAll(promises: List<ICommunicationPromise<*>>): ICommunicationPromise<Unit> {
        return combineAllToUnitPromise(promises.union(listOf(this)))
    }

    override fun addFailureListener(listener: (Throwable) -> Unit): ICommunicationPromise<T> {
        addCompleteListener { promise ->
            promise.cause()?.let { listener(it) }
        }
        return this
    }

    companion object {
        fun combineAllToUnitPromise(promises: Collection<ICommunicationPromise<*>>): ICommunicationPromise<Unit> {
            val unitPromise = CommunicationPromise<Unit>()
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
    }


}