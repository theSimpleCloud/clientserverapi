package eu.thesimplecloud.clientserverapi.server.client.connectedclient

import io.netty.channel.Channel
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.DefaultConnection
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.NettyServer

class ConnectedClient<T : IConnectedClientValue>(private val channel: Channel, private val nettyServer: NettyServer<T>) : DefaultConnection(nettyServer.packetManager, nettyServer.packetResponseManager), IConnectedClient<T> {

    private var clientValue: T? = null

    override fun getClientValue(): T? = clientValue

    override fun setClientValue(connectedClientValue: T) {
        this.clientValue = connectedClientValue
    }

    override fun getNettyServer(): INettyServer<T> = nettyServer

    override fun getChannel(): Channel? = channel


}