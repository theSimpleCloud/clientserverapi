package eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise

import io.netty.util.concurrent.GenericFutureListener


interface IConnectionPromiseListener<T> : GenericFutureListener<IConnectionPromise<T>> {

    override fun operationComplete(future: IConnectionPromise<T>)

}


