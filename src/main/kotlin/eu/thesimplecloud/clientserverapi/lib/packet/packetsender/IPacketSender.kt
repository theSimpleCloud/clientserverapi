package eu.thesimplecloud.clientserverapi.lib.packet.packetsender

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface IPacketSender {

    /**
     * Sends a query to the connection and returns a [ICommunicationPromise].
     * @return a [ICommunicationPromise] to wait for the result.
     */
    fun <T : Any> sendQuery(packet: IPacket, expectedResponseClass: Class<T>, timeout: Long = 200): ICommunicationPromise<T>

    /**
     * Sends a query to the connection and returns a [ICommunicationPromise].
     * @return a [ICommunicationPromise] to wait for the result.
     */
    fun sendUnitQuery(packet: IPacket, timeout: Long = 200): ICommunicationPromise<Unit> = sendQuery(packet, Unit::class.java, timeout)

}

/**
 * Sends a query to the connection and returns a [ICommunicationPromise].
 * @return a [ICommunicationPromise] to wait for the result.
 */
inline fun <reified T : Any> IPacketSender.sendQuery(packet: IPacket, timeout: Long = 200): ICommunicationPromise<T> = sendQuery(packet, T::class.java, timeout)