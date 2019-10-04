package eu.thesimplecloud.clientserverapi.lib.connection

import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import io.netty.channel.Channel
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise
import java.io.File

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
     * Returns the [ICommunicationBootstrap] of this connection
     * [INettyServer] when this connection is instantiated on the server side
     * [INettyClient] when this connection is instantiated on the client side
     */
    fun getCommunicationBootstrap() : ICommunicationBootstrap

    /**
     * Sends a file via this connection
     * @param file the file to send
     * @param savePath the path where the file should be saved
     */
    fun sendFile(file: File, savePath: String): IConnectionPromise<Unit>


}