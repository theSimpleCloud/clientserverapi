package eu.thesimplecloud.clientserverapi.server

import eu.thesimplecloud.clientserverapi.lib.packetmanager.IPacketManager
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.IClientManager
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

interface INettyServer<T : IConnectedClientValue> {

    /**
     * Starts the server
     */
    fun start()

    /**
     * Registers all packets in the specified packages
     */
    fun registerPacketsByPackage(vararg packages: String)

    /**
     * Stops the server
     */
    fun shutdown()

    /**
     * Returns the [IClientManager] for this server
     */
    fun getClientManager(): IClientManager<T>

    /**
     * Returns the [IPacketManager] for this server
     */
    fun getPacketManager(): IPacketManager

    /**
     * Returns weather the server is active (listening)
     */
    fun isActive(): Boolean

}