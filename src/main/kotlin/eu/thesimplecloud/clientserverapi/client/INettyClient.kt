package eu.thesimplecloud.clientserverapi.client

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection

interface INettyClient : IConnection {

    /**
     * Start the client
     */
    fun start()

    /**
     * Stops the client
     */
    fun disconnect()

    /**
     * Returns weather the client is running.
     * This will be set to true when start is called.
     * To check if the the client is connected use [IConnection.isOpen]
     */
    fun isRunning(): Boolean

}