package eu.thesimplecloud.clientserverapi.lib.packet.packetsender

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetpromise.IPacketPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.IPacketResponseHandler

interface IPacketSender {


    /**
     * Sends a query to the connection and returns a [IPacketPromise].
     * @return a [IPacketPromise] to wait for the result.
     */
    fun sendQuery(packet: IPacket): IPacketPromise<Unit>

    /**
     * Sends a query to the connection and returns a [IPacketPromise].
     * @return a [IPacketPromise] to wait for the result.
     */
    fun <T : Any> sendQuery(packet: IPacket, packetResponseFunction: (IPacket) -> T?): IPacketPromise<T>


    /**
     * Sends a query to the connection and returns a [IPacketPromise].
     * @return a [IPacketPromise] to wait for the result.
     */
    fun <T : Any> sendQuery(packet: IPacket, packetResponseHandler: IPacketResponseHandler<T>): IPacketPromise<T>

}