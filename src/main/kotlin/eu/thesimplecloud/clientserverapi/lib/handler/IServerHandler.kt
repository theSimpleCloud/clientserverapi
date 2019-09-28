package eu.thesimplecloud.clientserverapi.lib.handler

import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

interface IServerHandler<T : IConnectedClientValue> {

    fun onServerStarted(nettyServer: NettyServer<T>)

    fun onServerStartException(nettyServer: NettyServer<T>, ex: Throwable)

    fun onServerShutdown(nettyServer: NettyServer<T>)

}