package eu.thesimplecloud.clientserverapi.lib.packet.packetpromise

import io.netty.util.concurrent.GenericFutureListener


interface IPacketPromiseListener<T> : GenericFutureListener<IPacketPromise<T>> {

    override fun operationComplete(future: IPacketPromise<T>)

}


