package eu.thesimplecloud.clientserverapi.lib.handler

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection

class DefaultConnectionHandler : IConnectionHandler {
    override fun onConnectionActive(connection: IConnection) {
    }

    override fun onFailure(connection: IConnection, ex: Throwable) {
        throw ex
    }

    override fun onConnectionInactive(connection: IConnection) {
    }

}