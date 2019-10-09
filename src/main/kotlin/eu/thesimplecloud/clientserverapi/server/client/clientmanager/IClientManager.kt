package eu.thesimplecloud.clientserverapi.server.client.clientmanager

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import io.netty.channel.ChannelHandlerContext
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

interface IClientManager<T : IConnectedClientValue> {

    /**
     * Returns the client registered to the specified [ChannelHandlerContext] or null if there is no client registered to the specified [ChannelHandlerContext]
     */
    fun getClient(ctx: ChannelHandlerContext): IConnectedClient<T>?

    /**
     * Sends the specified packet to all connected clients.
     */
    fun sendPacketToAllClients(packet: IPacket) = getClients().forEach { it.sendQuery(packet) }
    /**
     * Returns a list containing all connected clients.
     */
    fun getClients(): List<IConnectedClient<T>>

}