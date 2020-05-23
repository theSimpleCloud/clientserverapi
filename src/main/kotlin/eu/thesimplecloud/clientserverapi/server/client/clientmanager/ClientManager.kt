package eu.thesimplecloud.clientserverapi.server.client.clientmanager

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.ConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.CopyOnWriteArrayList

class ClientManager<T : IConnectedClientValue>(private val nettyServer: NettyServer<T>) : IClientManager<T> {
    private val clients = CopyOnWriteArrayList<IConnectedClient<T>>()

    fun addClient(ctx: ChannelHandlerContext): IConnection {
        val connection = ConnectedClient<T>(ctx.channel(), nettyServer)
        this.clients.add(connection)
        return connection
    }

    override fun getClient(ctx: ChannelHandlerContext) = this.clients.firstOrNull { it.getChannel() == ctx.channel() }

    override fun getClientByClientValue(clientValue: IConnectedClientValue): IConnectedClient<T>? = this.clients.firstOrNull { it.getClientValue() == clientValue }

    fun removeClient(ctx: ChannelHandlerContext): IConnection? {
        val client = getClient(ctx)
        client?.let { this.clients.remove(it) }
        return client
    }

    override fun getClients(): Collection<IConnectedClient<T>> = clients.filter { it.isOpen() }

}