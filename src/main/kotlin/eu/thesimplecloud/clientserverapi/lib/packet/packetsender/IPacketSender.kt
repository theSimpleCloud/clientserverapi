package eu.thesimplecloud.clientserverapi.lib.packet.packetsender

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler.IPacketResponseHandler

interface IPacketSender {

    /**
     * Sends a query to the connection and returns a [ICommunicationPromise].
     * @return a [ICommunicationPromise] to wait for the result.
     */
    fun <T : Any> sendQuery(packet: IPacket, expectedResponseClass: Class<T>): ICommunicationPromise<T>

    /**
     * Sends a query to the connection and returns a [ICommunicationPromise].
     * @return a [ICommunicationPromise] to wait for the result.
     */
    fun sendUnitQuery(packet: IPacket): ICommunicationPromise<Unit> = sendQuery(packet, Unit::class.java)

}

/**
 * Sends a query to the connection and returns a [ICommunicationPromise].
 * @return a [ICommunicationPromise] to wait for the result.
 */
inline fun <reified T : Any> IPacketSender.sendQuery(packet: IPacket): ICommunicationPromise<T> = sendQuery(packet, T::class.java)