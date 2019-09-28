package eu.thesimplecloud.clientserverapi.lib.handler

import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

class DefaultServerHandler<T : IConnectedClientValue> : IServerHandler<T> {
    override fun onServerStarted(nettyServer: NettyServer<T>) {
    }

    override fun onServerStartException(nettyServer: NettyServer<T>, ex: Throwable) {
    }

    override fun onServerShutdown(nettyServer: NettyServer<T>) {
    }

}