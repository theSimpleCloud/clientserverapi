package eu.thesimplecloud.clientserverapi.lib.connection

import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.server.INettyServer
import io.netty.channel.Channel
import java.io.File
import java.net.InetSocketAddress

interface IConnection : IPacketSender {

    /**
     * Returns the channel of this connection or null if the connection is not connected.
     * @return the channel or null if the connection is not connected.
     */
    fun getChannel(): Channel?

    /**
     * Returns whether the connection is open
     * @return true if the connection is open
     */
    fun isOpen(): Boolean {
        return getChannel() != null && getChannel()?.isActive ?: false
    }

    /**
     * Return whether the connection close was intended
     */
    fun wasConnectionCloseIntended(): Boolean

    /**
     * Returns the [ICommunicationBootstrap] of this connection
     * [INettyServer] when this connection is instantiated on the server side
     * [INettyClient] when this connection is instantiated on the client side
     */
    fun getCommunicationBootstrap(): ICommunicationBootstrap

    /**
     * Sends a file via this connection
     * @param file the file to send
     * @param savePath the path where the file should be saved
     */
    fun sendFile(file: File, savePath: String, timeout: Long): ICommunicationPromise<Unit>

    /**
     * Closes this [IConnection]
     */
    fun closeConnection(): ICommunicationPromise<Unit> {
        if (getChannel() == null || !getChannel()!!.isOpen) throw IllegalStateException("Connection already closed.")
        val connectionPromise = CommunicationPromise<Unit>(2000)
        getChannel()?.close()?.addListener { connectionPromise.trySuccess(Unit) }
        return connectionPromise
    }

    /**
     * Returns the host of this [IConnection] or null if the [IConnection] is not open.
     */
    fun getHost(): String? {
        if (!isOpen()) return null
        return (getChannel()!!.remoteAddress() as InetSocketAddress).hostString
    }


}