package eu.thesimplecloud.clientserverapi.lib.handler

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection

interface IConnectionHandler {

    /**
     * Called when a connection is now active
     * client side -> when the client is connected to the server
     * server side -> when a client connects to the server
     */
    fun onConnectionActive(connection: IConnection)

    /**
     * Called when an exception was thrown
     */
    fun onFailure(connection: IConnection, ex: Throwable)

    /**
     * Called when a connection disconnects
     * client side -> when the client disconnects form the server
     * server side -> when a client disconnects from the server
     */
    fun onConnectionInactive(connection: IConnection)


}