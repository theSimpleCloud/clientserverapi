package eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise

import io.netty.util.concurrent.*

class CommunicationPromise<T>(val eventExecutor: EventExecutor): DefaultPromise<T>(), ICommunicationPromise<T> {

    override fun addResultListener(listener: (T?) -> Unit): ICommunicationPromise<T> {
        addCommunicationPromiseListener(object: ICmmunicationPromiseListener<T> {
            override fun operationComplete(future: ICommunicationPromise<T>) {
                listener(future.get())
            }
        })
        return this
    }

    override fun addCommunicationPromiseListener(listener: (ICommunicationPromise<T>) -> Unit): ICommunicationPromise<T> {
        addCommunicationPromiseListener(object: ICmmunicationPromiseListener<T> {
            override fun operationComplete(future: ICommunicationPromise<T>) {
                listener(future)
            }
        })
        return this
    }

    override fun addCommunicationPromiseListener(listener: ICmmunicationPromiseListener<T>): ICommunicationPromise<T> {
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
        return this.eventExecutor
    }

    override fun setSuccess(result: T): ICommunicationPromise<T> {
        super.setSuccess(result)
        return this
    }



}