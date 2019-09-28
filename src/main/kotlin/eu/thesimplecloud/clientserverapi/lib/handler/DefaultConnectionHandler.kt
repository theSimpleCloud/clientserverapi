package eu.thesimplecloud.clientserverapi.lib.handler

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import java.io.IOException

class DefaultConnectionHandler : IConnectionHandler {
    override fun onConnectionActive(connection: IConnection) {
    }

    override fun onFailure(connection: IConnection, ex: Throwable) {
    }

    override fun onConnectionInactive(connection: IConnection) {
    }

}