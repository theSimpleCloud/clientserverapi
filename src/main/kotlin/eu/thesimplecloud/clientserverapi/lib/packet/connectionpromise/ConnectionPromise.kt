package eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise

import io.netty.util.concurrent.*
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection

class ConnectionPromise<T>(val eventExecutor: EventExecutor): DefaultPromise<T>(), IConnectionPromise<T> {

    override fun addResultListener(listener: (T?) -> Unit): IConnectionPromise<T> {
        addConnectionPromiseListener(object: IConnectionPromiseListener<T> {
            override fun operationComplete(future: IConnectionPromise<T>) {
                listener(future.get())
            }
        })
        return this
    }

    override fun addConnectionPromiseListener(listener: (IConnectionPromise<T>) -> Unit): IConnectionPromise<T> {
        addConnectionPromiseListener(object: IConnectionPromiseListener<T> {
            override fun operationComplete(future: IConnectionPromise<T>) {
                listener(future)
            }
        })
        return this
    }

    override fun addConnectionPromiseListener(listener: IConnectionPromiseListener<T>): IConnectionPromise<T> {
        super.addListener(listener)
        return this
    }

    override fun addConnectionPromiseListeners(vararg listeners: IConnectionPromiseListener<T>): IConnectionPromise<T> {
        super.addListeners(*listeners)
        return this
    }

    override fun addListener(listener: GenericFutureListener<out Future<in T>>?): IConnectionPromise<T> {
        super.addListener(listener)
        return this
    }

    override fun addListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): IConnectionPromise<T> {
        super.addListeners(*listeners)
        return this
    }

    override fun removeListener(listener: GenericFutureListener<out Future<in T>>?): IConnectionPromise<T> {
        super.removeListener(listener)
        return this
    }

    override fun removeListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): IConnectionPromise<T> {
        super.removeListeners(*listeners)
        return this
    }

    override fun sync(): IConnectionPromise<T> {
        super.sync()
        return this
    }

    override fun syncUninterruptibly(): IConnectionPromise<T> {
        super.syncUninterruptibly()
        return this
    }

    override fun await(): IConnectionPromise<T> {
        super.await()
        return this
    }

    override fun awaitUninterruptibly(): IConnectionPromise<T> {
        super.awaitUninterruptibly()
        return this
    }

    override fun executor(): EventExecutor {
        return this.eventExecutor
    }

    override fun setSuccess(result: T): IConnectionPromise<T> {
        super.setSuccess(result)
        return this
    }



}