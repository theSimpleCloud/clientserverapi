package eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise

import io.netty.util.concurrent.GenericFutureListener


interface ICmmunicationPromiseListener<T> : GenericFutureListener<ICommunicationPromise<T>> {

    override fun operationComplete(future: ICommunicationPromise<T>)

}


