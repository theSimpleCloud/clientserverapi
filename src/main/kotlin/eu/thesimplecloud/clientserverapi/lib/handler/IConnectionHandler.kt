package eu.thesimplecloud.clientserverapi.lib.handler

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection

interface IConnectionHandler {

    fun onConnectionActive(connection: IConnection)

    fun onFailure(connection: IConnection, ex: Throwable)

    fun onConnectionInactive(connection: IConnection)


}