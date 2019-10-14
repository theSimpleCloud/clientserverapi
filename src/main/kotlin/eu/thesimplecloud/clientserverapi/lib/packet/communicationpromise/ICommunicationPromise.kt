package eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise

import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import io.netty.util.concurrent.Promise


interface ICommunicationPromise<T> : Promise<T> {

    fun addResultListener(listener: (T?) -> Unit): ICommunicationPromise<T>

    fun addCommunicationPromiseListener(listener: (ICommunicationPromise<T>) -> Unit): ICommunicationPromise<T>

    fun addCommunicationPromiseListener(listener: ICmmunicationPromiseListener<T>): ICommunicationPromise<T>

    fun addCommunicationPromiseListeners(vararg listener: ICmmunicationPromiseListener<T>): ICommunicationPromise<T>

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