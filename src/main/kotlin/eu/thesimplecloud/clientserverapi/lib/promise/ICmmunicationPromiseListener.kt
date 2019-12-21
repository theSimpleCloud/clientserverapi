package eu.thesimplecloud.clientserverapi.lib.promise

import io.netty.util.concurrent.GenericFutureListener


interface ICmmunicationPromiseListener<T> : GenericFutureListener<ICommunicationPromise<T>> {

    override fun operationComplete(future: ICommunicationPromise<T>)

}


