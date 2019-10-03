package eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise

import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import io.netty.util.concurrent.Promise


interface IConnectionPromise<T> : Promise<T> {

    fun addResultListener(listener: (T?) -> Unit): IConnectionPromise<T>

    fun addConnectionPromiseListener(listener: (IConnectionPromise<T>) -> Unit): IConnectionPromise<T>

    fun addConnectionPromiseListener(listener: IConnectionPromiseListener<T>): IConnectionPromise<T>

    fun addConnectionPromiseListeners(vararg listener: IConnectionPromiseListener<T>): IConnectionPromise<T>

    override fun addListener(listener: GenericFutureListener<out Future<in T>>?): IConnectionPromise<T>

    override fun addListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): IConnectionPromise<T>

    override fun removeListener(listener: GenericFutureListener<out Future<in T>>?): IConnectionPromise<T>

    override fun removeListeners(vararg listeners: GenericFutureListener<out Future<in T>>?): IConnectionPromise<T>

    override fun sync(): IConnectionPromise<T>

    override fun syncUninterruptibly(): IConnectionPromise<T>

    override fun await(): IConnectionPromise<T>

    override fun awaitUninterruptibly(): IConnectionPromise<T>

    override fun setSuccess(result: T): IConnectionPromise<T>


}