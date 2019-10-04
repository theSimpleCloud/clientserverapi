package eu.thesimplecloud.clientserverapi.lib.packet.packetsender

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.IPacketResponseHandler

interface IPacketSender {


    /**
     * Sends a query to the connection and returns a [IConnectionPromise].
     * @return a [IConnectionPromise] to wait for the result.
     */
    fun sendQuery(packet: IPacket): IConnectionPromise<Unit> = sendQuery(packet, IPacketResponseHandler.getNullHandler())

    /**
     * Sends a query to the connection and returns a [IConnectionPromise].
     * @return a [IConnectionPromise] to wait for the result.
     */
    fun <T : Any> sendQuery(packet: IPacket, packetResponseFunction: (IPacket) -> T?): IConnectionPromise<T> {
        return sendQuery(packet, object : IPacketResponseHandler<T> {
            override fun handleResponse(packet: IPacket): T? {
                return packetResponseFunction(packet)
            }
        })
    }

    /**
     * Sends a query to the connection and returns a [IConnectionPromise].
     * @return a [IConnectionPromise] to wait for the result.
     */
    fun <T : Any> sendQuery(packet: IPacket, packetResponseHandler: IPacketResponseHandler<T>): IConnectionPromise<T>

}