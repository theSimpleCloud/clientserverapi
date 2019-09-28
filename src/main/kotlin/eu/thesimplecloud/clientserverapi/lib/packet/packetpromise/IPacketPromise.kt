package eu.thesimplecloud.clientserverapi.lib.packet.packetpromise

import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import io.netty.util.concurrent.Promise


interface IPacketPromise<T> : Promise<T> {

    fun addResultListener(listener: (T?) -> Unit): IPacketPromise<T>

    fun addPacketPromiseListener(listener: (IPacketPromise<T>) -> Unit): IPacketPromise<T>

    fun addPacketPromiseListener(listener: IPacketPromiseListener<T>): IPacketPromise<T>

    fun addPacketPromiseListeners(vararg listener: IPacketPromiseListener<T>): IPacketPromise<T>

    override fun addListener(listener: GenericFutureListener<out Future<in T>>?): IPacketPromise<T>

    override fun addListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): IPacketPromise<T>

    override fun removeListener(listener: GenericFutureListener<out Future<in T>>?): IPacketPromise<T>

    override fun removeListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): IPacketPromise<T>

    override fun sync(): IPacketPromise<T>

    override fun syncUninterruptibly(): IPacketPromise<T>

    override fun await(): IPacketPromise<T>

    override fun awaitUninterruptibly(): IPacketPromise<T>

    override fun setSuccess(result: T): IPacketPromise<T>


}