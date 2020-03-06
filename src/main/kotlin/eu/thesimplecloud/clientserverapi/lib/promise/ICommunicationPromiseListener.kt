package eu.thesimplecloud.clientserverapi.lib.promise

import io.netty.util.concurrent.GenericFutureListener


interface ICommunicationPromiseListener<T : Any> : GenericFutureListener<ICommunicationPromise<T>> {

    override fun operationComplete(future: ICommunicationPromise<T>)

}


