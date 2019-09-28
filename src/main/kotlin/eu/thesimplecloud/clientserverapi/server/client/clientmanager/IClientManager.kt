package eu.thesimplecloud.clientserverapi.server.client.clientmanager

import io.netty.channel.ChannelHandlerContext
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

interface IClientManager<T : IConnectedClientValue> {

    /**
     * Returns the client registered to the specified [ChannelHandlerContext] or null if there is no client registered to the specified [ChannelHandlerContext]
     */
    fun getClient(ctx: ChannelHandlerContext): IConnectedClient<T>?

    /**
     * Returns a list containing all connected clients.
     */
    fun getClients(): List<IConnectedClient<T>>

}