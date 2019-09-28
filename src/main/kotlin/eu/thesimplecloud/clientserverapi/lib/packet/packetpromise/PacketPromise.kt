package eu.thesimplecloud.clientserverapi.lib.packet.packetpromise

import io.netty.util.concurrent.*
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection

class PacketPromise<T>(val connection: IConnection): DefaultPromise<T>(), IPacketPromise<T> {

    override fun addResultListener(listener: (T?) -> Unit): IPacketPromise<T> {
        addPacketPromiseListener(object: IPacketPromiseListener<T> {
            override fun operationComplete(future: IPacketPromise<T>) {
                listener(future.get())
            }
        })
        return this
    }

    override fun addPacketPromiseListener(listener: (IPacketPromise<T>) -> Unit): IPacketPromise<T> {
        addPacketPromiseListener(object: IPacketPromiseListener<T> {
            override fun operationComplete(future: IPacketPromise<T>) {
                listener(future)
            }
        })
        return this
    }

    override fun addPacketPromiseListener(listener: IPacketPromiseListener<T>): IPacketPromise<T> {
        super.addListener(listener)
        return this
    }

    override fun addPacketPromiseListeners(vararg listeners: IPacketPromiseListener<T>): IPacketPromise<T> {
        super.addListeners(*listeners)
        return this
    }

    override fun addListener(listener: GenericFutureListener<out Future<in T>>?): IPacketPromise<T> {
        super.addListener(listener)
        return this
    }

    override fun addListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): IPacketPromise<T> {
        super.addListeners(*listeners)
        return this
    }

    override fun removeListener(listener: GenericFutureListener<out Future<in T>>?): IPacketPromise<T> {
        super.removeListener(listener)
        return this
    }

    override fun removeListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): IPacketPromise<T> {
        super.removeListeners(*listeners)
        return this
    }

    override fun sync(): IPacketPromise<T> {
        super.sync()
        return this
    }

    override fun syncUninterruptibly(): IPacketPromise<T> {
        super.syncUninterruptibly()
        return this
    }

    override fun await(): IPacketPromise<T> {
        super.await()
        return this
    }

    override fun awaitUninterruptibly(): IPacketPromise<T> {
        super.awaitUninterruptibly()
        return this
    }

    override fun executor(): EventExecutor {
        return connection.getChannel()!!.eventLoop()
    }

    override fun setSuccess(result: T): IPacketPromise<T> {
        super.setSuccess(result)
        return this
    }



}