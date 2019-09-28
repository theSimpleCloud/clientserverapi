package eu.thesimplecloud.clientserverapi.server.client.connectedclient

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.server.INettyServer

interface IConnectedClient<T : IConnectedClientValue> : IConnection {

    /**
     * Returns the [INettyServer] this client is connected to
     */
    fun getNettyServer(): INettyServer<T>

    /**
     * Returns the [IConnectedClientValue] of this [IConnectedClient] or null if no value was set.
     * Use this to get the stored object
     */
    fun getClientValue(): T?

    /**
     * Sets the [IConnectedClientValue] of this [IConnectedClient]
     * Use this to store an object in this [IConnectedClient]
     */
    fun setClientValue(connectedClientValue: T)

}