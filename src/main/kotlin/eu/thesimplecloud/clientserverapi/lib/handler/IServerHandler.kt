package eu.thesimplecloud.clientserverapi.lib.handler

import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

interface IServerHandler<T : IConnectedClientValue> {

    /**
     * Called when the server was started.
     */
    fun onServerStarted(nettyServer: NettyServer<T>)

    /**
     * Called when an exception was caught
     */
    @Throws(Throwable::class)
    fun onServerStartException(nettyServer: NettyServer<T>, ex: Throwable)

    /**
     * Called when the server is shut down
     */
    fun onServerShutdown(nettyServer: NettyServer<T>)

}